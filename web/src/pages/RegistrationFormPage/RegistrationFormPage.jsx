import React, {useEffect, useState} from "react";
import {useSearchParams} from "react-router-dom";

function HomePage(props) {

    const [searchParams, setSearchParams] = useSearchParams();
    let form = searchParams.get("formData");
    const [formData, setFormData] = useState(JSON.parse(atob(form)));
    const [state, setState] = useState({
        hasAdminRole: false,
        hasSupervisorRole: false,
        hasUserRole: false,
        hasReadPermission: false,
        hasReadWritePermission: false,
        fromEntityFirstName: formData.fromEntityFirstName,
        fromEntityLastName: formData.fromEntityLastName,
        fromEntityLegalName: formData.fromEntityLegalName,
        fromEntityOib: formData.fromEntityOib,
        forEntityIps: formData.forEntityIps,
        forEntityLegalName: formData.forEntityLegalName,
        toEntityFirstName: formData.toEntityFirstName,
        toEntityLastName: formData.toEntityLastName,
        toEntityLegalName: formData.toEntityLegalName,
        toEntityOib: formData.toEntityOib,
        serviceRequestId: '',
        responseUrl:"",
        cancelUrl:"",
        permissions: []
    }
    );

    const hasKeyPair = function(state, key, value) {
        return state && state.permissions
            .filter( x => x.key === key && x.value === value).length === 1;
    }

    const handleChange = (e, key, value) => {
        let checked = e.target.checked;
        let permissionCopy = [...state.permissions]
        if(hasKeyPair(state, key, value) === false){
            permissionCopy.push({key: key, value: value, description: '', validFrom: null, validTo: null, valueDescription: ''})
        } else if(hasKeyPair(state, key, value) === true) {
            let index = permissionCopy.findIndex( x => x.key === key && x.value === value);
            permissionCopy.splice(index, 1);
        }
        if(key === 'ULOGA' && value === 'admin'){
            setState({...state, hasAdminRole: checked, permissions: permissionCopy})
        } else if (key === 'ULOGA' && value === 'supervizor'){
            setState({...state, hasSupervisorRole: checked, permissions: permissionCopy})
        } else if(key === 'ULOGA' && value === 'korisnik'){
            setState({...state, hasUserRole: checked, permissions: permissionCopy})
        } else if(key === 'PRAVO' && value === 'read'){
            setState({...state, hasReadPermission: checked, permissions: permissionCopy})
        } else if (key === 'PRAVO' && value === 'write'){
            setState({...state, hasReadWritePermission: checked, permissions: permissionCopy})
        }
    }

    const initPermissions = (hasAdminRole, hasSupervisorRole, hasUserRole, hasReadPermission, hasReadWritePermission) => {
        let initialPermissions = [];
        if(hasAdminRole)
            initialPermissions.push({key: 'ULOGA', value: 'admin', description: '', validFrom: null, validTo: null, valueDescription: ''})
        if(hasSupervisorRole)
            initialPermissions.push({key: 'ULOGA', value: 'supervizor', description: '', validFrom: null, validTo: null, valueDescription: ''})
        if(hasUserRole)
            initialPermissions.push({key: 'ULOGA', value: 'korisnik', description: '', validFrom: null, validTo: null, valueDescription: ''})
        if(hasReadPermission)
            initialPermissions.push({key: 'PRAVO', value: 'read', description: '', validFrom: null, validTo: null, valueDescription: ''})
        if(hasReadWritePermission)
            initialPermissions.push({key: 'PRAVO', value: 'wire', description: '', validFrom: null, validTo: null, valueDescription: ''})

        return initialPermissions;
    }

    useEffect(() => {
        console.log(state, 'stejt');
    }, [state]);

    useEffect(() => {
        const hasAdminRole = hasKeyPair(formData,'ULOGA', 'admin');
        const hasSupervisorRole = hasKeyPair(formData,'ULOGA', 'supervizor');
        const hasUserRole = hasKeyPair(formData,'ULOGA', 'korisnik');
        const hasReadPermission = hasKeyPair(formData,'PRAVO', 'read');
        const hasReadWritePermission = hasKeyPair(formData,'PRAVO', 'write');
        const permissions = initPermissions(hasAdminRole, hasSupervisorRole, hasUserRole, hasReadPermission, hasReadWritePermission)

        setState({...state,
            hasAdminRole: hasAdminRole,
            hasSupervisorRole: hasSupervisorRole,
            hasUserRole: hasUserRole,
            hasReadPermission: hasReadPermission,
            hasReadWritePermission: hasReadWritePermission,
            fromEntityFirstName: formData.fromEntityFirstName,
            fromEntityLastName: formData.fromEntityLastName,
            fromEntityLegalName: formData.fromEntityLegalName,
            fromEntityOib: formData.fromEntityOib,
            forEntityIps: formData.forEntityIps,
            forEntityLegalName: formData.forEntityLegalName,
            toEntityFirstName: formData.toEntityFirstName,
            toEntityLastName: formData.toEntityLastName,
            toEntityLegalName: formData.toEntityLegalName,
            toEntityOib: formData.toEntityOib,
            permissions: permissions
        });

    }, []);

    return (
        <div className={"registration-form-page"}>
            <div className="col-md-8 offset-md-2">
                <div className="card card-outline-secondary" style={{marginTop: '32px'}}>
                    <div className="card-header">
                        <h3 className="mb-0">Dodjeljivanje dopuštenja</h3>
                    </div>
                    <div className="card-body">
                        <form className="form" method="post" role="form" action='http://localhost:8080/authorization/submit' method='post'>
                            <div className="form-group row">
                                <label className="col-lg-3 col-form-label form-control-label">Opunomoćitelj</label>
                                <div className="col-lg-9">
                                    <input className="form-control" type="text" value={state.fromEntityFirstName + ' ' + state.fromEntityLastName + ', ' + state.fromEntityOib + ', ' +  state.fromEntityLegalName} disabled/>
                                </div>
                            </div>
                            <div className="form-group row">
                                <label className="col-lg-3 col-form-label form-control-label">Daje se ovlaštenje
                                    za:</label>
                                <div className="col-lg-9">
                                    <input className="form-control" type="text" value={state.forEntityLegalName + ', ' + state.forEntityIps } disabled />
                                </div>
                            </div>
                            <div className="form-group row">
                                <label className="col-lg-3 col-form-label form-control-label">Opunomoćenik</label>
                                <div className="col-lg-9">
                                    <input className="form-control" type="text" value={state.toEntityFirstName + ' ' + state.toEntityLastName + ', ' + state.toEntityOib + ', ' +  state.toEntityLegalName} disabled/>
                                </div>
                            </div>
                            <div className="form-group row">
                                <label className="col-lg-3 col-form-label form-control-label">Rola</label>
                                <div className="form-check form-check-inline">
                                    <label className="form-check-label">
                                        <input
                                            className="form-check-input"
                                            type="checkbox"
                                            checked={state.hasAdminRole}
                                            onChange={(e) => handleChange(e, 'ULOGA','admin')}
                                        />
                                        Administrator
                                    </label>
                                </div>
                                <div className="form-check form-check-inline">
                                    <label className="form-check-label">
                                        <input
                                            className="form-check-input"
                                            type="checkbox"
                                            checked={state.hasSupervisorRole}
                                            onChange={(e) => handleChange(e, 'ULOGA','supervizor')}
                                        />
                                        Supervizor
                                    </label>
                                </div>
                                <div className="form-check form-check-inline disabled">
                                    <label className="form-check-label">
                                        <input
                                            className="form-check-input"
                                            type="checkbox"
                                            checked={state.hasUserRole}
                                            onChange={(e) => handleChange(e, 'ULOGA','korisnik')}
                                        />
                                        Korisnik
                                    </label>
                                </div>
                            </div>
                            <div className="form-group row">
                                <label className="col-lg-3 col-form-label form-control-label">Ovlast</label>
                                <div className="form-check form-check-inline">
                                    <label className="form-check-label">
                                        <input
                                            className="form-check-input"
                                            type="checkbox"
                                            checked={state.hasReadPermission}
                                            onChange={(e) => handleChange(e, 'PRAVO','read')}
                                        />
                                        Čitanje
                                    </label>
                                </div>
                                <div className="form-check form-check-inline">
                                    <label className="form-check-label">
                                        <input
                                            className="form-check-input"
                                            type="checkbox"
                                            checked={state.hasReadWritePermission}
                                            onChange={(e) => handleChange(e, 'PRAVO','write')}
                                        />
                                        Čitanje/Pisanje
                                    </label>
                                </div>
                            </div>
                            <div className="form-group" style={{ display: 'flex', flexDirection: 'row', alignItems: 'flex-end', justifyContent: 'center'}}>
                                <input className="btn btn-primary" type="submit" value="Potvrdi"/>
                                <input className="btn btn-primary" id="cancelBtn" value="Odustani" style={{marginLeft: '4px'}}/>
                            </div>
                                <input type='hidden' name='serviceRequestId' value={formData.serviceRequestId} />
                                <input type='hidden' name='responseUrl' value={formData.responseUrl}  />
                                <input type='hidden' name='b64permissions' value={btoa(JSON.stringify(state.permissions))} />
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default HomePage;