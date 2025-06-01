import style from '../../styles/register.module.css'
import AuthField from "../authentication/AuthField";

import 'bootstrap/dist/css/bootstrap.min.css'
import ErrorLabel from "../authentication/ErrorLabel";

// check if a username format is valid
function nameValidation(text) {
    return text.length !== 0;
}

// check if a password format is valid
function passwordValidation(text) {
    return text.length !== 0 && text.length >= 8 && text.length <= 60;
}


function RegisterForm(props) {
    function validatePasswordValidation(text) {
        return text === props.formData.password;
    }

    // user name error
    const usernameError = "Please enter a valid username, username can't be blank";
    const passwordError = "Your password must contain between 8 and 60 characters";
    const displayNameError = "Please enter a valid display name, display name can't be blank";
    const passwordValidationError = "The password doses not match, or the validate password is empty";


    const imageError = "\u2612 Please enter a valid image name";
    /// checking if input is image
    const handleInputFile = (e) => {
        const name = e.target.files[0].name;

        if (!name.match(/\.(jpg|jpeg|png|gif|svg)$/i)) {
            props.updateFormData('image', null);
            props.updateError("image", imageError);
            e.target.value = null;
            return;
        }
        props.updateFormData("image", e.target.files[0]);
    }


    return (
        <form
            onSubmit={props.handleRegister}
            autoComplete="off">

            <AuthField
                setter={props.updateFormData}
                value={props.formData.username}
                style={style}
                errorMessage={usernameError}
                id="username"
                type='text'
                placeholder="Username"
                conditon={nameValidation}
                error={props.errors.username}
                errorSetter={props.updateError}/>

            <AuthField
                setter={props.updateFormData}
                value={props.formData.displayName}
                style={style}
                errorMessage={displayNameError}
                id="displayName"
                type='text'
                placeholder="Display Name"
                conditon={nameValidation}
                error={props.errors.displayName}
                errorSetter={props.updateError}/>

            <AuthField
                setter={props.updateFormData}
                value={props.formData.password}
                style={style}
                errorMessage={passwordError}
                id="password"
                type='password'
                placeholder="Password"
                conditon={passwordValidation}
                error={props.errors.password}
                errorSetter={props.updateError}
            />

            <AuthField
                setter={props.updateFormData}
                value={props.formData.validatePassword}
                style={style}
                errorMessage={passwordValidationError}
                id="validatePassword"
                type='password'
                placeholder="Validate Password"
                conditon={validatePasswordValidation}
                error={props.errors.validatePassword}
                errorSetter={props.updateError}


            />

            <div style={{marginTop: '20px'}} className="input-group mb-3">
                <label className="input-group-text" htmlFor="inputGroupFile01">Upload User Image</label>
                <input onInput={handleInputFile} type="file" className="form-control" id="inputGroupFile01"
                       accept="image/jpg,image/jpeg,image/png,image/svg"/>
            </div>
            <ErrorLabel
                id="inputGroupFile01"
                className={style.errorTextInput}
                error={props.errors.image}
            />
            <button className={style.button}>Register</button>
        </form>

    );
}


export default RegisterForm;