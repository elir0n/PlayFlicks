import {useState} from "react";
import TextInput from "./TextInput";
import ErrorLabel from "./ErrorLabel";

function AuthField(props) {
    const [labelClass, setLabelClass] = useState(`${props.style.netflixInputTextPlaceholder}`);


// define the changing label class
    const handleOnBlur = (e) => {
        if (!props.conditon(e.target.value)) {
            props.errorSetter(props.id, `\u2612 ${props.errorMessage}`);
        } else {
            props.errorSetter(props.id, '');
        }

        if (props.value === '') {
            setLabelClass(`${props.style.netflixInputTextPlaceholder}`);
            return;
        }
        setLabelClass(`${props.style.netflixInputTextPlaceholderActive}`);
    };

    const handleOnFocus = () => setLabelClass(`${props.style.netflixInputTextPlaceholderActive}`);

    return (
        <>
            <TextInput
                onfocus={handleOnFocus}
                onBlur={handleOnBlur}

                setChange={props.setter}
                value={props.value}
                inputClass={props.style.netflixInputText}
                id={props.id}
                type={props.type}
                placeholder={props.placeholder}
                labelClass={labelClass}
            />
            <ErrorLabel
                className={props.style.errorTextInput}
                error={props.error}
            />
        </>
    );
}

export default AuthField;
