// get token from api server
async function getToken(name, password) {
    const requestOptions = {
        method: "post",
        headers: {
            'Content-Type': 'application/json'
        }, body: JSON.stringify({
            name: name,
            password: password
        })
    }

    try {
        return await fetch("http://localhost:3001/api/tokens/getToken", requestOptions);
    } catch (e) {
        console.error('api server is down');
        return null;
    }

}


// check if token is authenticated in api server
async function isAuthenticated(token) {
    const requestOptions = {
        method: "get",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
        },
    };

    let res;
    try {
        return await fetch('http://localhost:3001/api/tokens/auth', requestOptions);
    } catch (e) {
        console.error('api server is down');
        return null;
    }
}

async function createUser(name, password, image, displayName) {

    const formData = new FormData();

    formData.append('image', image);
    formData.append('name', name);
    formData.append('password', password);
    formData.append('displayName', displayName);

    const requestOptions = {
        method: "post",
        body: formData,
    };

    try {
        return await fetch("http://localhost:3001/api/users", requestOptions);
    } catch (e) {
        console.error('api server is down');
        return null;
    }

}

export {getToken, isAuthenticated, createUser};