import React from "react";
import { Button } from "reactstrap";
import { NavLink as RouterNavLink, Link } from "react-router-dom";
import ToolDescription from "./ToolDescription.js";
import ReactPlayer from "react-player";
import { useTranslation, Trans } from "react-i18next";
import Breadcrumb from "reactstrap/lib/Breadcrumb";
import BreadcrumbItem from "reactstrap/lib/BreadcrumbItem";

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
      <a href="https://www.chirpstack.io/gateway-bridge/" target="_blank" rel="noopener noreferrer">
          Chirpstack Gateway Bridge setup guide
      </a>
      <ol>
      <li>{t("chirpstack.gateway.bridge.ssh")} <code>ssh root@GATEWAY-IP-ADDRESS</code> {t("chirpstack.gateway.bridge.ip")}</li>
      <li>{t("chirpstack.gateway.bridge.website")}  
      <a href="https://artifacts.chirpstack.io/vendor/kerlink/ifemtocell/" target="_blank" rel="noopener noreferrer">
          website
      </a>
      {t("chirpstack.gateway.bridge.getlink")}
      </li>
      <li>{t("chirpstack.gateway.bridge.pastelink")}</li>
      <Breadcrumb>
      <BreadcrumbItem>
      <p>
        mkdir -p /user/.updates <br/>
        cd /user/.updates <br/> wget LINKLOCATION
      </p>
      </BreadcrumbItem>
      </Breadcrumb>
      <li>{t("chirpstack.gateway.bridge.commands")}</li>
      <Breadcrumb>
      <BreadcrumbItem>
      <p>
        sync <br/>
        kerosd -u <br/> reboot
      </p>
      </BreadcrumbItem>
      </Breadcrumb>
      <li>{t("chirpstack.gateway.bridge.reconnect")} <code>vi /user/etc/chirpstack-gateway-bridge/chirpstack-gateway-bridge.toml </code></li>
          <ol>
          <li>{t("chirpstack.gateway.packets.lorad4")} <code>udp_bind = "0.0.0.0:1700"</code></li>
          <li>{t("chirpstack.gateway.packets.lorad4")} <code>server="tcp://IPADDRESS:1883" </code> {t("chirpstack.gateway.bridge.raspip")}</li>
          </ol>
      <li>{t("chirpstack.gateway.packets.lorad5")}</li>
      <li>{t("chirpstack.gateway.bridge.restart")} <code>monit restart chirpstack-gateway-bridge </code></li>
      <li>{t("chirpstack.gateway.bridge.lorafwd")} <code>monit restart lorafwd</code></li>
      <li>{t("chirpstack.gateway.bridge.setup")}</li>
      <li>{t("chirpstack.gateway.bridge.getid")} <code>cat /var/run/lora/gateway-id.toml</code></li>
      </ol>

      <h2>{t("chirpstack.raspi.title")}</h2>
      <h3>{t("chirpstack.raspi.network.title")}</h3>
      <a href="https://www.chirpstack.io/network-server/install/debian/" target="_blank" rel="noopener noreferrer">
          Chirpstack Network Server setup guide
      </a>
      <ol>
        <li>{t("chirpstack.raspi.network.update")}</li>
        <Breadcrumb>
        <BreadcrumbItem>
        <p>
          sudo apt update <br/>
          sudo apt full-upgrade
        </p>
        </BreadcrumbItem>
        </Breadcrumb>
        <li>{t("chirpstack.raspi.network.install")}</li>
        <Breadcrumb>
        <BreadcrumbItem>
        <p>
          sudo apt install mosquitto <br/>
          sudo apt install postgresql <br/>
          sudo apt install redis-server</p>
        </BreadcrumbItem>
        </Breadcrumb>
        <li>{t("chirpstack.raspi.network.configure")} <code>sudo -u postgres psql</code></li>
        <ol>
          <li>{t("chirpstack.raspi.network.inside")}</li>
        <Breadcrumb>
        <BreadcrumbItem>
        <p>
                create role chirpstack_ns with login password 'dbpassword'; <br/>
                create database chirpstack_ns with owner chirpstack_ns; <br/>
                \q
        </p>
        </BreadcrumbItem>
        </Breadcrumb>
        </ol>
        <li>{t("chirpstack.raspi.network.verify")} <code>psql -h localhost -U chirpstack_ns -W chirpstack_ns</code></li>
        <li>{t("chirpstack.raspi.network.binary")}</li>
        <Breadcrumb>
        <BreadcrumbItem>
        <p>
          sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 1CE2AFD36DBCCA00 <br/>
          sudo echo "deb https://artifacts.chirpstack.io/packages/3.x/deb stable main" | sudo tee /etc/apt/sources.list.d/chirpstack.list <br/>
          sudo apt update
        </p>
        </BreadcrumbItem>
        </Breadcrumb>
        <li>{t("chirpstack.raspi.network.use")} <code>sudo apt install chirpstack-network-server</code></li>
        <li>{t("chirpstack.raspi.network.login")} <code>sudo su</code></li>
        <li>{t("chirpstack.raspi.network.config")} <code>/etc/chirpstack-network-server/chirpstack-network-server.toml</code></li>
        <ol>
          <li>{t("chirpstack.raspi.network.notexists")} <code>cp /etc/chirpstack-network-server/examples/chirpstack-network-server-eu868.toml /etc/chirpstack-network-server/chirpstack-network-server.toml</code></li>
          <li>{t("chirpstack.raspi.network.ensure")}</li>
          <ol>
            <li>{t("chirpstack.raspi.network.password")} <code>dsn="postgres://chirpstack_ns:PASSWORD@localhost/chirpstack_ns?sslmode=disable"</code></li>
            <li>{t("chirpstack.raspi.network.mqtt")} <code>server="tcp://localhost:1883"</code></li>
          </ol>
        <li>{t("chirpstack.raspi.network.exit")}</li>
        <li>{t("chirpstack.raspi.network.enable")} <code>udo systemctl restart chirpstack-network-server</code></li>
        <ol>
          <li>{t("chirpstack.raspi.network.status")} <code>sudo systemctl status chirpstack-network-server</code></li>
          <li>{t("chirpstack.raspi.network.log")} <code>journalctl -u chirpstack-network-server -f -n 50</code></li>
        </ol>
        </ol>
      </ol>
      <h3>{t("chirpstack.raspi.application.title")}</h3>
      <a href="https://www.chirpstack.io/application-server/" target="_blank" rel="noopener noreferrer">
          Chirpstack Application Server setup guide
      </a>
      <ol>

        <li>{t("chirpstack.raspi.application.setup")} <code>sudo -u postgres pqsl</code></li>
        <li>{t("chirpstack.raspi.network.inside")}</li>
        <Breadcrumb>
        <BreadcrumbItem>
        <p>
          create role chirpstack_as with login password 'dbpassword'; <br/>
          <br/>
          create database chirpstack_as with owner chirpstack_as; <br/>
<br/>
          \c chirpstack_as <br/>
          create extension pg_trgm; <br/>
          create extension hstore; <br/>
<br/>
          \q
        </p>
        </BreadcrumbItem>
        </Breadcrumb>
        <li>{t("chirpstack.raspi.network.verify")}<code>psql -h localhost -U chirpstack_as -W chirpstack_as</code></li>
        <li>{t("chirpstack.raspi.application.binary")}</li>
        <Breadcrumb>
        <BreadcrumbItem>
        <p>
          sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 1CE2AFD36DBCCA00 <br/>
          sudo echo "deb https://artifacts.chirpstack.io/packages/3.x/deb stable main" | sudo tee /etc/apt/sources.list.d/chirpstack.list <br/>
          sudo apt-get update
        </p>
        </BreadcrumbItem>
        </Breadcrumb>
        <li>{t("chirpstack.raspi.application.install")} <code>sudo apt-get install chirpstack-application-server</code></li>
        <li>{t("chirpstack.raspi.application.superuser")} <code>sudo su</code></li>
        <li>{t("chirpstack.raspi.application.config")} <code>/etc/chirpstack-application-server/chirpstack-application-server.toml</code></li>
        <ol>
          <li>{t("chirpstack.raspi.network.ensure")}</li>
          <li>{t("chirpstack.raspi.network.password")} <code>dsn="postgres://chirpstack_as:PASSWORD@localhost/chirpstack_as?sslmode=disable"</code></li>
          <li>{t("chirpstack.raspi.network.mqtt")} <code>server="tcp://localhost:1883"</code></li>
          <li>{t("chirpstack.raspi.application.getjwt")} <code>openssl rand -base64 32</code></li>
          <li>{t("chirpstack.raspi.application.setjwt")} <code>jwt_secret="TOKEN"</code></li>
        </ol>
        <li>{t("chirpstack.raspi.network.exit")} <code>exit</code></li>
        <li>{t("chirpstack.raspi.application.restart")} <code>sudo systemctl restart chirpstack-application-server</code></li>
        <ol>
          <li>{t("chirpstack.raspi.network.status")} <code>sudo systemctl status chirpstack-application-server</code></li>
          <li>{t("chirpstack.raspi.application.log")} <code>journalctl -u chirpstack-application-server -f -n 50</code></li>
        </ol>
        <li>{t("chirpstack.raspi.application.access")} 
      <a href="http://localhost:8080" target="_blank" rel="noopener noreferrer">
         http://localhost:8080 
      </a>
       {t("chirpstack.raspi.application.access2")} 
        </li>
      </ol>
      
      <Button className="float-right" tag={RouterNavLink} to="/server/connection" color="danger">
        Next: Chirpstack Web Interface
      </Button>
    </div>
  );
};

export default ServerSetup;
