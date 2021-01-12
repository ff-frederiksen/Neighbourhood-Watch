import React from "react";
import { Button } from "reactstrap";
import { NavLink as RouterNavLink, Link } from "react-router-dom";
import ToolDescription from "./ToolDescription.js";
import ReactPlayer from "react-player";
import { useTranslation, Trans } from "react-i18next";

const ServerSetup = props => {
  var stack = props.stackStatus;
  const { t } = useTranslation("server_v1-"+stack);
  return (
    <div>
      <h1>
        {t("chirpstack.title")}
        <span style={{ color: "grey", fontSize: "40%" }}>v1.0</span>
      </h1>
      <p>{t("chirpstack.intro.intro")}</p>
      <hr />
      <h2>{t("chirpstack.gateway.title")}</h2>
      <p><b>{t("chirpstack.kerlink")}</b></p>
      <p>{t("chirpstack.intro.1")}</p>
      <p>{t("chirpstack.intro.2")}</p>
      <p>{t("chirpstack.intro.3")}
      <a href="https://wikikerlink.fr" target="_blank" rel="noopener noreferrer">
          Kerlink Wiki
          </a>
         {t("chirpstack.intro.4")}
      </p>
      <h3>{t("chirpstack.gateway.packets.title")}</h3>
      <p>{t("chirpstack.gateway.packets.wiki")}
          <a href="https://wikikerlink.fr/wirnet-productline/doku.php?id=wiki:lora:keros_4.3.3:cpf_configuration" target="_blank" rel="noopener noreferrer">
          Kerlink Wiki Guide
          </a>
      </p>
      <p>{t("chirpstack.gateway.packets.preinstalled")}</p>
      <h4>Lorad</h4>
      <ol>
        <li>{t("chirpstack.gateway.packets.lorad1")} <code>cp /etc/lorad/wifc/EU868-FR.json /etc/lorad/lorad.json </code> {t("chirpstack.gateway.packets.lorad2")}</li>  
        <li>{t("chirpstack.gateway.packets.lorad3")} <code>vi /etc/default/lorad</code>
        <ol>
          <li>
            {t("chirpstack.gateway.packets.lorad4")} <code>CONFIGURATION_FILE </code> {t("chirpstack.gateway.packets.links")} <code>/etc/lorad/lorad.json</code>
          </li>
          <li>
            {t("chirpstack.gateway.packets.lorad4")} <code>DISABLE_LORAD=“no”</code>
          </li>
          </ol>
        </li>  
        <li>{t("chirpstack.gateway.packets.lorad5")}</li>  
        <li>{t("chirpstack.gateway.packets.lorad6")} <code>monit status lorad</code> 
          <ol>
          <li>{t("chirpstack.gateway.packets.lorad7")} <code>monit restart lorad</code></li>
          </ol>
        </li>
      </ol>  
      <h4>Lorafwd</h4>

      <ol>
        <li>{t("chirpstack.gateway.packets.lorafwd1")} <code>vi /etc/default/lorafwd </code></li>
        <ol>
          <li>{t("chirpstack.gateway.packets.lorad4")} <code>DISABLE_LORAFWD="no" </code> {t("chirpstack.gateway.packets.lorafwd2")}</li>
        </ol>
        <li>{t("chirpstack.gateway.packets.lorafwd3")} <code>vi /etc/lorafwd.toml </code></li>
        <ol>
          <li>{t("chirpstack.gateway.packets.lorafwd4")}</li>
          <li>{t("chirpstack.gateway.packets.lorafwd5")}</li>
          <li>{t("chirpstack.gateway.packets.lorafwd6")}</li>
        </ol>
      </ol>
      <p>{t("chirpstack.gateway.packets.enable")} <br/><code>klk_apps_config --activate-cpf --lns-server IPADDRESS --lns-dport 1700 --lns-uport 1700</code></p>

      <h3>{t("chirpstack.gateway.bridge.title")}</h3>

      

      <Button className="float-right" tag={RouterNavLink} to="/server/os" color="danger">
        Next: Raspberry Pi Setup
      </Button>
    </div>
  );
};

export default ServerSetup;
