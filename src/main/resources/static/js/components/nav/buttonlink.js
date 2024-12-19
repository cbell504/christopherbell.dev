import React from 'react';
import { useNavigate } from 'react-router-dom';

const ButtonLink = ({ to, className, children }) => {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(to);
    };

    return (
        <button type="button" className={className} onClick={handleClick}>
            {children}
        </button>
    );
};

export default ButtonLink;