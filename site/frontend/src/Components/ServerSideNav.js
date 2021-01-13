import React from "react";
import { Nav, NavItem, NavLink } from "reactstrap";
import { NavLink as RouterNavLink } from "react-router-dom";
import { useTranslation } from "react-i18next";

const ServerSideNav = props => {
  var stack = props.stackStatus;
  const { t } = useTranslation("server_v1-"+stack);


  function showLoraStack(){
   
    if (stack === "chirpstack"){
     
          return <NavLink tag={RouterNavLink} to="/server/chirpstack" activeClassName="active" className="text-muted">
            {t("navigation.chirpstackSetup")}
          </NavLink>
      
    }
    else{

          return <NavLink tag={RouterNavLink} to="/server/ttn" activeClassName="active" className="text-muted">
            {t("navigation.ttnSetup")}
          </NavLink>
    }
  }

  function showChirpInterface(){

    if (stack === "chirpstack"){
      return <NavLink tag={RouterNavLink} to="/server/chirpstack/webinterface" activeClassName="active" className="text-muted">
            {t("navigation.chirpstackWeb")}
          </NavLink>
    }
  }

  return (
    <div>
      <p>{t("navigation.hardware")}</p>
      <Nav vertical>
        <NavItem>
          <NavLink tag={RouterNavLink} to="/server/devices" className="text-muted">
            {t("navigation.devices")}
          </NavLink>
        </NavItem>
        <NavItem>
          <NavLink tag={RouterNavLink} to="/server/os" activeClassName="active" className="text-muted">
            {t("navigation.raspiSetup")}
          </NavLink>
        </NavItem>
        <NavItem>
          {showLoraStack()}
        </NavItem>
      </Nav>
      <hr />
      <p>{t("navigation.software")}</p>
      <Nav vertical>
        <NavItem>
          <NavLink tag={RouterNavLink} to="/server/connection" className="text-muted">
            {t("navigation.server")}
          </NavLink>
        </NavItem>
        <NavItem>
          <NavLink tag={RouterNavLink} to="/server/database" className="text-muted">
            {t("navigation.database")}
          </NavLink>
        </NavItem>
        <NavItem>
          <NavLink tag={RouterNavLink} to="/server/integration" className="text-muted">
            {t("navigation.integration")}
          </NavLink>
        </NavItem>
        <NavItem>
          {showChirpInterface()}
        </NavItem>
      </Nav>
    </div>
  );
};

export default ServerSideNav;
