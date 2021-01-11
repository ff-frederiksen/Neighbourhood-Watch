import React from "react";
import { Container, Row, Col } from "reactstrap";
import { Route, Switch, Redirect } from "react-router-dom";
import ServerSideNav from "./ServerSideNav";
import ServerWelcome from "./ServerWelcome";
import ServerDevices from "./ServerDevices";
import ServerTTN from "./ServerTTN";
import ServerRasOs from "./ServerRasOs.js";
import ServerConnection from "./ServerConnection.js";
import ServerDatabase from "./ServerDatabase.js";
import ServerIntegration from "./ServerIntegration.js";
import EditText from "./EditText";
import { useTranslation } from "react-i18next";

const ServerPage = props => {


  const stack = props.stackStatus;
  const { t } = useTranslation("general-"+stack);
  return (
    <>
      <Container className="themed-container clearfix" fluid={true}>
        <Row>
          <Col sm="2" style={{ padding: "1.5rem", paddingTop: "2rem", borderRight: "1px solid #0000001a" }}>
            <ServerSideNav stackStatus={stack}></ServerSideNav>
          </Col>
          <hr />
          <Col sm="7" style={{ padding: "3rem", borderTop: "1px solid #0000001a" }}>
            <Switch>
              <Route exact path="/server" render={props => (<ServerWelcome {...props} stackStatus={stack}/>)}/>
              <Route exact path="/server/devices" render={props => (<ServerDevices {...props} stackStatus={stack}/>)}/>
              <Route exact path="/server/chirpstack" render={props => (<ServerTTN {...props} stackStatus={stack}/>)}/>
              <Route exact path="/server/os" render={props => (<ServerRasOs {...props} stackStatus={stack}/>)}/>
              <Route exact path="/server/connection" render={props => (<ServerConnection {...props} stackStatus={stack}/>)}/>
              <Route exact path="/server/database" render={props => (<ServerDatabase {...props} stackStatus={stack}/>)}/>
              <Route exact path="/server/integration" render={props => (<ServerIntegration {...props} stackStatus={stack}/>)}/>
              <Route exact path="/server/ttn" render={props => (<ServerTTN {...props} stackStatus={stack}/>)}/>
              <Redirect to="/server" />
            </Switch>
          </Col>
          <Col sm="1"></Col>
          <Col
            sm="2"
            style={{
              padding: "1.5rem",
              paddingTop: "2rem",
              borderLeft: "1px solid #0000001a",
              borderTop: "1px solid #0000001a"
            }}
          >
            <p>{t("general:improve.intro")}</p>
            <EditText
              buttonLabel={t("improve.button")}
              link={t("improve.popup.linkToServer")}
              fileName="server_v1.json"
              stackStatus={stack}
            ></EditText>
          </Col>
        </Row>
      </Container>
    </>
  );
};

export default ServerPage;
