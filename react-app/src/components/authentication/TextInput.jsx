function LoginFormField(props) {


    const handleChange = (e) => props.setChange(props.id, e.target.value);

    const handleLabelDoubleClick = () => props.inputRef.current.focus();
    return (
        <div style={{position: 'relative'}}>
            <input
                onFocus={props.onfocus}
                onBlur={props.onBlur}
                className={props.inputClass}
                id={props.id}
                value={props.value}
                type={props.type}
                onChange={handleChange}
            />
            <label
                onDoubleClick={handleLabelDoubleClick}
                htmlFor={props.id}
                className={props.labelClass}
            >
                {props.placeholder}
            </label>
        </div>
    );
}


export default LoginFormField;