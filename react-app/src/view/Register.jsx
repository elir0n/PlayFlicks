import {useState} from "react";
import {useNavigate} from "react-router-dom";
import Header from "../components/register/Header";
import RegisterForm from "../components/register/RegisterForm";
import {createUser, getToken} from "../viewmodel/Authenticate";
import style from '../styles/register.module.css';
import Logo from "../components/unsigned/Logo";

function Register() {
    const navigate = useNavigate();

    // Consolidate form state
    const [formData, setFormData] = useState({
        username: "",
        password: "",
        displayName: "",
        validatePassword: "",
        image: null
    });

    // Consolidate error states
    const [errors, setErrors] = useState({
        username: "",
        password: "",
        displayName: "",
        validatePassword: "",
        image: ""
    });

    // Alert state
    const [alert, setAlert] = useState({
        show: false,
        message: ""
    });

    // Error messages
    const errorMessages = {
        username: "\u2612 Please enter a valid username, username can't be blank",
        password: "\u2612 Your password must contain between 8 and 60 characters",
        displayName: "\u2612 Please enter a valid display name, display name can't be blank",
        validatePassword: "\u2612 The password does not match, or the validate password is empty",
        image: "\u2612 Please enter a valid image name"
    };

    const validateForm = () => {
        const newErrors = {};

        // Check required fields
        Object.keys(errors).forEach((key) => {
            if (errors[key]) {
                newErrors[key] = errors[key];
            }
        });
        
        Object.keys(formData).forEach(field => {
            if (!formData[field]) {
                newErrors[field] = errorMessages[field];
            }
        });

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0; // Fixed: using length instead of size
    };

    const handleRegister = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        const result = await createUser(
            formData.username,
            formData.password,
            formData.image,
            formData.displayName
        );

        if (result.success) {
            const token = await getToken(formData.username, formData.password);
            localStorage.setItem('token', token);
            navigate('/home');
            return;
        }

        setAlert({
            show: true,
            message: result.message === "user already exists"
                ? "Username already exists, try a different one"
                : "API server is down, please try later"
        });
    };

    const updateError = (field, value) => {
        setErrors(prev => ({...prev, [field]: value}));
    }
    
    const updateFormData = (field, value) => {
        setFormData(prev => ({...prev, [field]: value})); // Fixed: using prev instead of formData
        // Clear error when a user starts typing
        if (errors[field]) {
            setErrors(prev => ({...prev, [field]: ""}));
        }
    };

    return (
        <>
            <Logo/>
            <Header/>
            <div className={style.registerDiv}>
                {alert.show && (
                    <div className="alert alert-danger" role="alert">
                        {alert.message}
                    </div>
                )}

                <h1 className={style.registerTitle}>
                    Register now and enjoy all the available movies
                </h1>

                <RegisterForm
                    formData={formData}
                    errors={errors}
                    updateError={updateError}
                    updateFormData={updateFormData}
                    handleRegister={handleRegister}
                />
            </div>
        </>
    );
}

export default Register;