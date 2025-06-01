function ErrorLabel(props) {
    return (
        <label
            className={props.className}>{props.error}</label>
    );
}

export default ErrorLabel;