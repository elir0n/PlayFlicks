
import style from '../../styles/login.module.css'

function BlackBoxWrapper(props) {
    return (
        <div className={style.blackBoxWrapper}>{props.children}</div>
    );
}


export default BlackBoxWrapper;