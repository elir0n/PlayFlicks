import BlackBoxWrapper from "../components/login/BlackBoxWrapper";
import BackgroundWrapper from "../components/login/BackgroundWrapper";
import LoginForm from "../components/login/LoginForm";
import ErrorAuthentication from "../components/login/ErrorAuthentication";
import RegisterPrompt from "../components/login/RegisterPrompt";
import styles from "../styles/login.module.css";
import '../styles/login.module.css'
import {useState} from "react";
import {getToken} from "../viewmodel/Authenticate";
import {useNavigate} from "react-router-dom";
import Logo from "../components/unsigned/Logo";

// make the login component
function Login() {

    const [formData, setFormData] = useState({
        username: "",
        password: "",
    })

    const [display, setDisplay] = useState('none');
    const navigate = useNavigate();


    const [errors, setErrors] = useState({
        username: "",
        password: "",
    });

    const errorMessages = {
        username: "\u2612 Please enter a valid username, username can't be blank",
        password: "\u2612 Your password must contain between 8 and 60 characters",
    }

    const updateFormData = (field, value) => {
        setFormData(prev => ({...formData, [field]: value}));
        if (errors[field]) {
            setErrors(prev => ({...prev, [field]: ''}));
        }
    }

    const updateError = (field, value) => {
        setErrors(prev => ({...prev, [field]: value}));
    };


    const validateForm = () => {
        const newErrors = {};
        let hasError = false;

        // Check required fields

        Object.keys(errors).forEach((field) => {
            if (errors[field]) {
                hasError = true;
                newErrors[field] = errors[field];
            }
        })
        Object.keys(formData).forEach(field => {
            if (!formData[field]) {
                newErrors[field] = errorMessages[field];
                hasError = true;
            }
        });

        setErrors(newErrors);
        return !hasError;
    };
    const handleSignIn = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }


        const token = await getToken(formData.username, formData.password);

        // checking the response validity
        if (token === null) {
            setDisplay('block');
            return;
        }

        localStorage.setItem('token', token);

        navigate('/home');
    };

    return (
        <>
        <Logo/>
        <BackgroundWrapper>
            <BlackBoxWrapper>
                <ErrorAuthentication display={display}/>

                <h2 className={styles.signInText}>Sign In</h2>
                <LoginForm
                    formData={formData}
                    errors={errors}
                    updateError={updateError}
                    updateFormData={updateFormData}
                    handleSignIn={handleSignIn}
                />
                <RegisterPrompt/>
            </BlackBoxWrapper>
        </BackgroundWrapper>
        </>
    );
}

export default Login;