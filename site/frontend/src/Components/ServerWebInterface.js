import React from "react";
import { useTranslation} from "react-i18next";
import loginPage from "../Images/login-page.png"

const ServerSetup = props => {
  var stack = props.stackStatus;
  const { t } = useTranslation("server_v1-"+stack);
  return (
    <div>
    <h1>{t("chirpstack.app.title")}</h1>
    <h3>Login</h3>
    <p>{t("chirpstack.app.intro")} <code>SERVER-IP:8080</code> <br/>
    
    {t("chirpstack.app.web.login")}
    </p>
    <img
      src={loginPage}
      alt="Login page for the web interface"
      style={{ height: "auto", maxWidth: "100%" }}
    />
    <p>
    {t("chirpstack.app.web.default")} <br/>
    {t("chirpstack.app.web.dashboard")}
    </p>
    <p>
    <h3>Organization</h3>
    <p>{t("chirpstack.app.web.organizationintro")}</p>
    <ol>
    <li>{t("chirpstack.app.web.organization1")}</li>
    <li>{t("chirpstack.app.web.organization2")}</li>
    <li>{t("chirpstack.app.web.organization3")}</li>
    </ol>
    </p>
    <h3>Network-servers</h3>
    <p>{t("chirpstack.app.web.networkintro")}
    <ol>
    <li>{t("chirpstack.app.web.network1")}</li>
    <li>{t("chirpstack.app.web.network2")}</li>
    <li>{t("chirpstack.app.web.network3")}</li>
    <li>{t("chirpstack.app.web.network4")} <code>localhost:8000</code></li>
    <li>{t("chirpstack.app.web.network5")}</li>
    </ol>
    </p>
    <h3>Service-profiles</h3>
    <p>{t("chirpstack.app.web.serviceintro")}</p>
    <ol>
    <li>{t("chirpstack.app.web.create")}</li>
    <li>{t("chirpstack.app.web.service1")}</li>
    <li>{t("chirpstack.app.web.service2")}</li>
    <li>{t("chirpstack.app.web.service3")}</li>
    <li>{t("chirpstack.app.web.service4")}</li>
    <li>{t("chirpstack.app.web.service5")}</li>
    <li>{t("chirpstack.app.web.service6")}</li>
    </ol>
    <h3>Gateways</h3>
    <p>{t("chirpstack.app.web.gatewayintro")}</p>
    <ol>
    <li>{t("chirpstack.app.web.create")}</li>
    <li>{t("chirpstack.app.web.gateway1")}</li>
    <li>{t("chirpstack.app.web.gateway2")}</li>
    <li>{t("chirpstack.app.web.gateway3")}</li>
    <li>{t("chirpstack.app.web.gateway4")}</li>
    <li>{t("chirpstack.app.web.gateway5")}</li>
    <li>{t("chirpstack.app.web.gateway6")}</li>
    </ol>
    <h3>Application</h3>
    <p>{t("chirpstack.app.web.applicationintro")}</p>
    <ol>
    <li>{t("chirpstack.app.web.application1")}</li>
    <li>{t("chirpstack.app.web.application2")}</li>
    <li>{t("chirpstack.app.web.application3")}</li>
    <li>{t("chirpstack.app.web.application4")}</li>
    <li>{t("chirpstack.app.web.application5")}</li>
    </ol>
    <h3>{t("chirpstack.app.devices.title")}</h3>
    <p>{t("chirpstack.app.devices.intro")}</p>
    <ol>
    <li>{t("chirpstack.app.devices.add1")}</li>
    <li>{t("chirpstack.app.devices.add2")}</li>
    <li>{t("chirpstack.app.devices.add3")}</li>
    <li>{t("chirpstack.app.devices.add4")}</li>
    <li>{t("chirpstack.app.devices.add5")}</li>
    <li>{t("chirpstack.app.devices.add6")}</li>
    <li>{t("chirpstack.app.devices.add7")}</li>
    <li>{t("chirpstack.app.devices.add8")}</li>
    <li>{t("chirpstack.app.devices.add9")}</li>
    <li>{t("chirpstack.app.devices.getID")}</li>
    <li>{t("chirpstack.app.devices.success")}</li>
    </ol>
    </div>
  );
};

export default ServerSetup;
