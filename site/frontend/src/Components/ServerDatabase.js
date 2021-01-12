import React from "react";
import { Button } from "reactstrap";
import { NavLink as RouterNavLink, Link } from "react-router-dom";
import { Breadcrumb, BreadcrumbItem } from "reactstrap";
import ToolDescription from "./ToolDescription.js";
import { useTranslation, Trans } from "react-i18next";
import databaseIMGttn from "../Images/database-ttn.png";
import databaseIMGChirpstack from "../Images/database-chirpstack.png"
import mainMethod from "../Images/mainMethod.png";

const ServerDatabase = props => {
  
  var stack = props.stackStatus;
  const { t } = useTranslation("server_v1-"+stack);

  if (stack === "chirpstack"){
  return (
    <div>
      <h1>
        {t("database.title")}
        <span style={{ color: "grey", fontSize: "40%" }}>v1.0</span>
      </h1>
      <hr />
      <h3>{t("database.update.title")}</h3>
      <p>
        <Trans i18nKey="server_v1-chirpstack:database.update.intro">
          To edit or update the database, open the file <code>CreateHomes.java</code> at:
        </Trans>
      </p>
      <Breadcrumb>
        <BreadcrumbItem active>src/main/java/backend/CreateHomes.java</BreadcrumbItem>
      </Breadcrumb>
      <p>{t("database.update.scroll")}</p>
      <img
        className="photo-logo"
        src={databaseIMGChirpstack}
        alt="Sample database setup"
        style={{ height: "auto", maxWidth: "100%" }}
      />
      <p>
        <b>{t("database.update.remember")}</b>
        <Breadcrumb>
          <BreadcrumbItem>
            openssl rand -base64 12
          </BreadcrumbItem>
        </Breadcrumb>
        {t("database.update.save")}
      </p>
      <Button className="float-right" tag={RouterNavLink} to="/server/integration" color="danger">
        {t("database.next")}
      </Button>
    </div>
  );
  }

return (
    <div>
      <h1>
        {t("database.title")}
        <span style={{ color: "grey", fontSize: "40%" }}>v1.0</span>
      </h1>
      <hr />
      <h3>{t("database.update.title")}</h3>
      <p>
        <Trans i18nKey="server_v1:database.update.intro">
          To edit or update the database, open the file <code>SetupExample.java</code> at:
        </Trans>
      </p>
      <Breadcrumb>
        <BreadcrumbItem active>src/main/java/dtu.exampleFile/SetupExample.java</BreadcrumbItem>
      </Breadcrumb>
      <p>{t("database.update.scroll")}</p>
      <img
        className="photo-logo"
        src={databaseIMGttn}
        alt="Sample database setup"
        style={{ height: "auto", maxWidth: "100%" }}
      />
      <p>
        <b>{t("database.update.remember")}</b>
      </p>
      <h3>{t("database.implement.title")}</h3>
      <p>
        <Trans i18nKey="server_v1:database.implement.intro">
          To run database updates like described above, you need to run the file <code>Main_Method.java</code> at:
        </Trans>
      </p>
      <Breadcrumb>
        <BreadcrumbItem active>src/main/java/dtu.dtu.alarmSystemBackend/Main_Method.java</BreadcrumbItem>
      </Breadcrumb>
      <p>
        <Trans i18nKey="server_v1:database.implement.scroll">
          Scroll down to the method similar to the one shown below and make sure to
          <ToolDescription id="slashes" name="uncomment" description={t("database.implement.tooltip")} /> the line
          <i>SetupExample.testing(args);</i>
        </Trans>
      </p>
      <img
        className="photo-logo"
        src={mainMethod}
        alt="run the database setup"
        style={{ height: "auto", maxWidth: "100%" }}
      />
      <p>
        <Trans i18nKey="server_v1:database.implement.run">
          Then proceed to run the application as described in the
          <Link to="/server/connection" className="alert-link">
            server connection
          </Link>
          section. The databases should now be updated. Remember to comment the line out again.
        </Trans>
      </p>
      <Button className="float-right" tag={RouterNavLink} to="/server/integration" color="danger">
        {t("database.next")}
      </Button>
    </div>
  );

};

export default ServerDatabase;
