import React, { Component } from 'react';
import './Hello.css';
import { Link } from 'react-router-dom';

class Hello extends Component {
    getUrlParameter(name) {
        name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
        var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');

        var results = regex.exec(this.props.location.search);
        return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
    };
    render() {
        const code = this.getUrlParameter('code');
        console.log("code:"+code);
        return (
            <div>
                <h1 className="title">
                    Hello
                </h1>
                <Link to="/ui"><button className="go-back-btn btn btn-primary" type="button">Go Back</button></Link>
            </div>
        );
    }
}

export default Hello;