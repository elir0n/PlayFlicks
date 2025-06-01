
import style from '../../styles/login.module.css'

function BackgroundWrapper(props) {
    return (
        <div className={style.background}>
            {props.children}
        </div>

    )
}

export default BackgroundWrapper;