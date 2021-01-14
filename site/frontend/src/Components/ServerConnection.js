import React from "react";
import { Button } from "reactstrap";
import { Breadcrumb, BreadcrumbItem, Toast, ToastBody, ToastHeader } from "reactstrap";
import { NavLink as RouterNavLink } from "react-router-dom";
import ToolDescription from "./ToolDescription.js";
import { useTranslation, Trans } from "react-i18next";
import { CodeBlock } from "react-code-blocks";
import importEclipse from "../Images/importEclipse.png";

const ServerConnection = props => {

  var stack = props.stackStatus;
  const { t } = useTranslation("server_v1-"+stack);

  if (stack === "chirpstack"){
  return (
    <div>
      <h1>
        {t("connection.title")}
        <span style={{ color: "grey", fontSize: "40%" }}>v1.0</span>
      </h1>
      <hr />
      <h3>{t("connection.downTitle")}</h3>
      <p>
        <Trans i18nKey="server_v1-chirpstack:connection.downSec">
          You need to download the alarm system files at
          <a href="https://github.com/ff-frederiksen/Neighbourhood-Watch" target="_blank" rel="noopener noreferrer">
          https://github.com/ff-frederiksen/Neighbourhood-Watch 
          </a>
          . Click "Clone or download" -> "Download ZIP". Once this has downloaded, unzip the folder and place it where
          you like.
        </Trans>
      </p>
      <h3>{t("connection.java.title")}</h3>
      <p>
        <Trans i18nKey="server_v1-chirpstack:connection.java.intro">
          To manipulate and compile the server files NWA recommends to use an
          <ToolDescription id="ide" name="IDE" description={t("connection.java.tooltipIDE")} /> optimized for
          <ToolDescription id="java" name="Java" description={t("connection.java.tooltipJava")} />
          development - particularly Eclipse. Eclipse can be downloaded from 
          <a href="https://www.eclipse.org/downloads/" target="_blank" rel="noopener noreferrer">
            https://www.eclipse.org/downloads/
          </a>
          .
        </Trans>
      </p>
      <h3>Chirpstack connection</h3>
      <p>
        {t("connection.chirpstack.server")} 
      </p>
      <Breadcrumb>
        <BreadcrumbItem>
        <p>
        private static String chirpstack_ip = "RASPBERRY-PI-IP"; <br/> 
        private static String mqtt_port = "MQTT-PORT"; <br/>
        private static String chirpstack_port = "CHIRPSTACK-PORT"; <br/>
        public static int application_id = APPLICATION-ID; <br/>
        public static int organization_id = ORGANIZATION-ID; <br/>
        public static String device_profile = "Device-profile-string";<br/>
        </p>
        </BreadcrumbItem>
      </Breadcrumb>
      in the file 
      <Breadcrumb>
        <BreadcrumbItem>
        Neighbourhood-Watch/Chirpstack/server/src/main/java/backend/Server.java
        </BreadcrumbItem> 
      </Breadcrumb>
      <p>
      <dl>
        <dt>Chirpstack_ip</dt>
        <dd>{t("connection.chirpstack.chirpstack_ip")}</dd> 
        <dt>mqtt_port</dt>
        <dd>{t("connection.chirpstack.mqtt_port")}</dd> 
        <dt>Chirpstack_port</dt>
        <dd>{t("connection.chirpstack.chirpstack_port")}</dd> 
        <dt>Application_id</dt>
        <dd>{t("connection.chirpstack.application_id")}</dd> 
        <dt>Organization_id</dt>
        <dd>{t("connection.chirpstack.organization_id")}</dd> 
        <dt>Device_profile</dt>
        <dd>{t("connection.chirpstack.device_profile")}</dd> 
        <p>{t("connection.chirpstack.getdevice_profile")} <code>curl -X GET --header 'Accept: application/json' --header 'Grpc-Metadata-Authorization: Bearer TOKEN' 'IP:8080/api/device-profiles?limit=5'</code>
        {t("connection.chirpstack.getid")}
        </p>
      </dl>
      </p>
      <Button className="float-right" tag={RouterNavLink} to="/server/database" color="danger">
        Next: Database Setup
      </Button>
    </div>
  );
}

return (
    <div>
      <h1>
        {t("connection.title")}
        <span style={{ color: "grey", fontSize: "40%" }}>v1.0</span>
      </h1>
      <hr />
      <h3>{t("connection.downTitle")}</h3>
      <p>
        <Trans i18nKey={"server_v1-"+stack+":connection.downSec"}>
          You need to download the alarm system files at
          <a href="https://github.com/ff-frederiksen/Neighbourhood-Watch" target="_blank" rel="noopener noreferrer">
          https://github.com/ff-frederiksen/Neighbourhood-Watch 
          </a>
          . Click "Clone or download" -> "Download ZIP". Once this has downloaded, unzip the folder and place it where
          you like.
        </Trans>
      </p>
      <h3>{t("connection.java.title")}</h3>
      <p>
        <Trans i18nKey="server_v1-ttn:connection.java.intro">
          To manipulate and compile the server files NWA recommends to use an
          <ToolDescription id="ide" name="IDE" description={t("connection.java.tooltipIDE")} /> optimized for
          <ToolDescription id="java" name="Java" description={t("connection.java.tooltipJava")} />
          development - particularly Eclipse. Eclipse can be downloaded from 
          <a href="https://www.eclipse.org/downloads/" target="_blank" rel="noopener noreferrer">
            https://www.eclipse.org/downloads/
          </a>
          . In Eclipse, perform the following actions:
        </Trans>
      </p>
      <Breadcrumb>
        <BreadcrumbItem active>
          File -> Import -> General/File System -> Browse -> path_to_folder/nwa/alarm-system/server
        </BreadcrumbItem>
      </Breadcrumb>
      <p>{t("connection.java.eclipse")}</p>
      <img
        className="photo-logo"
        src={importEclipse}
        alt="Import of NWA Server"
        style={{ paddingBottom: "20px", height: "auto", maxWidth: "100%" }}
      />
      <h3>{t("connection.ttn.title")}</h3>
      <p>
        <Trans i18nKey="server_v1-ttn:connection.ttn.intro">
          After the project has been imported, expand it and open the file <code>MSGRecver.java</code> at:
        </Trans>
      </p>
      <Breadcrumb>
        <BreadcrumbItem active>src/main/java/dtu.ttnCommunication/MSGRecver.java</BreadcrumbItem>
      </Breadcrumb>
      <p>{t("connection.ttn.strings")}</p>
      <div className="p-3 my-2 rounded">
        <Toast>
          <ToastHeader>MSGRecver.java</ToastHeader>
          <ToastBody>
            ...
            <br /> String region = "&lt;your_region&gt;";
            <br /> String appID = "&lt;your_app_name&gt;";
            <br /> appAccessKey = "&lt;64bit_acces_key&gt;";
            <br /> ...
          </ToastBody>
        </Toast>
      </div>
      <p>{t("connection.ttn.check")}</p>
      <Breadcrumb>
        <BreadcrumbItem active>Run as -> Java Application -> Main_Method</BreadcrumbItem>
      </Breadcrumb>
      <p>
        <Trans i18nKey="server_v1-ttn:connection.ttn.output">
          This should cause the Console to print out "<b>connected to the backend...</b>". Make sure you close it down
          again.
        </Trans>
      </p>
      <Button className="float-right" tag={RouterNavLink} to="/server/database" color="danger">
        Next: Database Setup
      </Button>
    </div>
  );
};

export default ServerConnection;
