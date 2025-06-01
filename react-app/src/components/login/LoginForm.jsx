import AuthField from "../authentication/AuthField";
import SignInButton from "./SignInButton";
import style from '../../styles/login.module.css'


// check if a username format is valid
function userNameValidation(text) {
    return text.length !== 0;
}

// check if a password format is valid
function passwordValidation(text) {
    return text.length !== 0 && text.length >= 8 && text.length <= 60;
}


function LogInForm(props) {
    const usernameError = "Please enter a valid username, username can't be blank";
    const passwordError = "Your password must contain between 8 and 60 characters";
    return (
        <form onSubmit={props.handleSignIn} autoComplete="off">
            <AuthField
                style={style}
                errorMessage={usernameError}
                conditon={userNameValidation}
                id='username'
                type="text"
                placeholder="Username"
                value={props.formData.username}
                setter={props.updateFormData}
                error={props.errors.username}
                errorSetter={props.updateError}
            />
            <AuthField
                style={style}
                errorMessage={passwordError}
                conditon={passwordValidation}
                id='password'
                type="password"
                placeholder="Password"
                value={props.formData.password}
                setter={props.updateFormData}
                error={props.errors.password}
                errorSetter={props.updateError}
            />
            <SignInButton/>
        </form>
    );

}

export default LogInForm;