import React from "react";
import { Container, Row, Col } from "reactstrap";
import { Route, Switch, Redirect } from "react-router-dom";
import AlarmSideNav from "./AlarmSideNav";
import AlarmWelcome from "./AlarmWelcome";
import AlarmDesign from "./AlarmDesign";
import AlarmCPLidar from "./AlarmCPLidar";
import AlarmCPPir from "./AlarmCPPir";
import AlarmCPUltra from "./AlarmCPUltra";
import AlarmSNLidar from "./AlarmSNLidar";
import AlarmSNPir from "./AlarmSNPir";
import AlarmSNUltra from "./AlarmSNUltra";
import AlarmSNMikro from "./AlarmSNMikro";
import AlarmSNGas from "./AlarmSNGas";
import AlarmSNFugt from "./AlarmSNFugt";
import AlarmSNHall from "./AlarmSNHall";
import EditText from "./EditText";
import { useTranslation } from "react-i18next";


const AlarmPage = props => {

  var stack = props.stackStatus;
  const { t } = useTranslation("general-" + stack);

  return (
    <>
      <Container className="themed-container clearfix" fluid={true}>
        <Row>
          <Col sm="2" style={{ padding: "1.5rem", paddingTop: "2rem", borderRight: "1px solid #0000001a" }}>
            <AlarmSideNav stackStatus={stack}> </AlarmSideNav>
          </Col>
          <hr />
          <Col sm="7" style={{ padding: "3rem", borderTop: "1px solid #0000001a" }}>
            <Switch>
              <Route exact path="/alarms" render={props => (
                <AlarmWelcome {...props} stackStatus={stack} />)} />
              <Route exact path="/alarms/design" render={props => (
                <AlarmDesign {...props} stackStatus={stack} />)} />
              <Route exact path="/alarms/cp-lidar" render={props => (
                <AlarmCPLidar {...props} stackStatus={stack} />)} />
              <Route exact path="/alarms/cp-pir" render={props => (
                <AlarmCPPir {...props} stackStatus={stack} />)} />
              <Route exact path="/alarms/cp-ultrasonic" render={props => (
                <AlarmCPUltra {...props} stackStatus={stack} />)} />
              <Route exact path="/alarms/sn-lidar" render={props => (
                <AlarmSNLidar {...props} stackStatus={stack} />)} />
              <Route exact path="/alarms/sn-pir" render={props => (
                <AlarmSNPir {...props} stackStatus={stack} />)} />
              <Route exact path="/alarms/sn-ultrasonic" render={props => (
                <AlarmSNUltra {...props} stackStatus={stack} />)} />
              <Route exact path="/alarms/sn-mikro" render={props => (
                <AlarmSNMikro {...props} stackStatus={stack} />)} />
              <Route exact path="/alarms/sn-gas" render={props => (
                <AlarmSNGas {...props} stackStatus={stack} />)} />
              <Route exact path="/alarms/sn-fugt" render={props => (
                <AlarmSNFugt {...props} stackStatus={stack} />)} />
              <Route exact path="/alarms/sn-hall" render={props => (
                <AlarmSNHall {...props} stackStatus={stack} />)} />
              <Redirect to="/alarms" />
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
            <p>{t("general-" + stack + ":improve.intro")}</p>
            <EditText
              buttonLabel={t("general-" + stack + ":improve.button")}
              link={t("improve.popup.linkToAlarm")}
              fileName="alarm_v1-ttn.json"
              stackStatus={stack}
            ></EditText>
          </Col>
        </Row>
      </Container>
    </>
  );
};

export default AlarmPage;