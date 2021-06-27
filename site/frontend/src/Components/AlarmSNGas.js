import React, { useState } from "react";
import { Nav, NavItem, NavLink, TabContent, TabPane, Col, Row, Button, ButtonDropdown, DropdownToggle, DropdownMenu, DropdownItem } from "reactstrap";
import { useTranslation } from "react-i18next";

import ToolDescription from "./ToolDescription.js";
import classnames from 'classnames';
import gasDiagram from "../Images/Fritzing/Images/NodeGas.png";
import newFile from "../Images/arduino-new.png";
import libManager from "../Images/arduino-lib-man.png";
import ttnCode from "../Images/arduino-ttn-code.png";
import chirpstackCode from "../Images/arduino-chirpstack-code.png";
import serial from "../Images/arduino-serial.png";
import AlarmSNBasic from "./AlarmSNBasic.js";
import AlarmSNUse from "./AlarmSNUse.js";

const AlarmSNGas = props => {

    var stack = props.stackStatus;
    const { t } = useTranslation("alarm_v1-" + stack);
    const [activeTab, setActiveTab] = useState('1');

    const toggle = tab => {
        if (activeTab !== tab) setActiveTab(tab);
    }
    const [dropdownOpen, setDropdownOpen] = useState(false);

    const toggleVersion = () => setDropdownOpen(prevState => !prevState);

    function codeModification() {

        if (stack === "chirpstack") {

            return <div>
                <p>{t("guides.software.upload-chirpstack1")}
                    <a
                        href="https://neighbourhood-watch-lora.herokuapp.com/server/chirpstack/webinterface"
                        target="_blank"
                        rel="noopener noreferrer"
                    >
                        Chirpstack Web Interface Guide
                    </a>
                </p>
                <p>{t("guides.software.upload-chirpstack2")}<code> 0xf9, 0x92, 0x1c, 0x7e, 0x30, 0x7c, 0x84, 0x7d </code>
                    {t("guides.software.upload-chirpstack3")}
                </p>

                <img
                    src={chirpstackCode}
                    alt="Code section with the DEVUI and APPKEY variables visible"
                    style={{
                        height: "auto", width: "100%", display: "block", marginLeft: "auto",
                        marginRight: "auto", maxWidth: "1000px", paddingBottom: "30px"
                    }}
                />
            </div>

        }
        else {
            return <div>
                <p>{t("guides.software.upload-ttn")}</p>
                <img
                    src={ttnCode}
                    alt="Code section with NWKSKEY, APPSKEY, and DEVADDR which must be specified"
                    style={{
                        height: "auto", width: "100%", display: "block", marginLeft: "auto",
                        marginRight: "auto", maxWidth: "1000px", paddingBottom: "30px"
                    }}
                />
            </div>

        }

    }

    function isTTN() {
        if (stack === "chirpstack") {
            return <li>{t("guides.software.list-chirpstack")}</li>
        }
        else {
            return <li>{t("guides.software.list-ttn")}</li>
        }
    }
    return (
        <div>
            <h1>{t("navigation.sn-gas")}
                <ButtonDropdown isOpen={dropdownOpen} size="sm" toggle={toggleVersion} style={{ paddingLeft: "10px", display: "inline-block" }}>
                    <DropdownToggle outline caret>
                        {t("guides.version")}
                    </DropdownToggle>
                    <DropdownMenu>
                        <DropdownItem>1.0</DropdownItem>
                    </DropdownMenu>
                </ButtonDropdown>
            </h1>

            <p>{t("guides.sn-gas-intro")}
            </p>
            <p>
                {t("guides.tooltip-explain0")}
                <ToolDescription id="tooltip-sngas" name={t("tooltip.tooltipname")} description={t("tooltip.tooltipdesc")} />
                {t("guides.tooltip-explain1")}
            </p>

            <Nav tabs style={{ marginBottom: "30px" }}>
                <NavItem>
                    <NavLink
                        className={classnames({ active: activeTab === '1' })}
                        onClick={() => { toggle('1'); }}
                    >
                        {t("guides.tab-hardware")}
                    </NavLink>
                </NavItem>
                <NavItem>
                    <NavLink
                        className={classnames({ active: activeTab === '2' })}
                        onClick={() => { toggle('2'); }}
                    >
                        {t("guides.tab-software")}
                    </NavLink>
                </NavItem>
                <NavItem>
                    <NavLink
                        className={classnames({ active: activeTab === '3' })}
                        onClick={() => { toggle('3'); }}
                    >
                        {t("guides.tab-use-sn")}
                    </NavLink>
                </NavItem>
            </Nav>
            <TabContent activeTab={activeTab}>
                <TabPane tabId="1">
                    <Row>
                        <Col>
                            <img
                                src={gasDiagram}
                                alt="Gas Sensor Node hookup diagram"
                                style={{ height: "auto", maxWidth: "100%" }}
                            />
                        </Col>
                        <Col>
                            <h3>{t("guides.parts-list")}</h3>
                            <ul>
                                <li>1 {t("guides.part.uno")}</li>
                                <li>1 {t("guides.part.lora")}</li>
                                <li>1 {t("guides.part.gas")}</li>
                                <li>7 {t("guides.part.m2m")}</li>
                            </ul>
                        </Col>
                    </Row>
                    <AlarmSNBasic stackStatus={stack} />
                    <h3>{t("guides.gas-title")}</h3>
                    <img
                        src={gasDiagram}
                        alt="Gas Sensor Node hookup diagram"
                        style={{
                            height: "auto", width: "100%", display: "block", marginLeft: "auto",
                            marginRight: "auto", maxWidth: "700px"
                        }} />

                    <ul>
                        <li>{t("guides.gas-list0")}</li>
                        <li>{t("guides.gas-list1")}</li>
                        <li>{t("guides.gas-list2")}</li>
                        <li>{t("guides.gas-list3")}</li>
                        <li>{t("guides.gas-list4")}</li>
                        <li>{t("guides.gas-list5")}</li>
                    </ul>
                    <p>{t("guides.sn-hardware-outro")}</p>
                    <Button className="float-right" color="danger" onClick={() => { toggle('2'); window.scrollTo(0, 0); }}>{t("guides.next-tab")}{t("guides.tab-software")}</Button>
                </TabPane>
                <TabPane tabId="2">
                    <h2>{t("guides.software.ide-title")}</h2>
                    <p>{t("guides.software.ide-intro")}</p>
                    <h3>{t("guides.parts-list")}</h3>
                    <ul>
                        <li>{t("guides.software.a-gas-sn")}</li>
                        {isTTN()}
                        <li>{t("guides.software.list-computer")}</li>
                        <li>{t("guides.software.list-cable")}</li>
                    </ul>
                    <p>
                        {t("guides.software.ide-install0")}{" "}
                        <a href="https://www.arduino.cc/en/main/software" target="_blank" rel="noopener noreferrer">
                            Arduino IDE
                        </a>{" "}
                        {t("guides.software.ide-install1")}
                        <ToolDescription id="ide-sngas" name={t("tooltip.idename")} description={t("tooltip.idedesc")} />
                        {t("guides.software.ide-install2")}
                    </p>
                    <p>{t("guides.software.new-file")}</p>

                    <img
                        src={newFile}
                        alt="New button in Arduino IDE for creating new files"
                        style={{
                            height: "auto", width: "100%", display: "block", marginLeft: "auto",
                            marginRight: "auto", maxWidth: "400px", paddingBottom: "30px"
                        }}
                    />
                    <p>
                        {t("guides.software.sn-code0")}{" "}
                        <a href="https://raw.githubusercontent.com/ff-frederiksen/Neighbourhood-Watch/main/Chirpstack/alarm/sensor_nodes/Sensor_node_CO2/Sensor_node_CO2.ino" target="_blank" rel="noopener noreferrer">
                            {t("guides.software.sn-code-gas")}
                        </a>{" "}
                    </p>

                    <h3>{t("guides.software.lib-title")}</h3>
                    <p>
                        {t("guides.software.lib-intro0")}
                        <ToolDescription id="lib-sngas" name={t("tooltip.libname")} description={t("tooltip.libdesc")} />
                        {t("guides.software.lib-intro1")}
                    </p>

                    <h4>{t("guides.software.lib-ide-title")}</h4>
                    <p>{t("guides.software.lib-ide-intro0")}</p>

                    <img
                        src={libManager}
                        alt="Navigation of arduino IDE tabs to find Library Manager: (Sketch -> Include Library -> Manage Libraries)"
                        style={{
                            height: "auto", width: "100%", display: "block", marginLeft: "auto",
                            marginRight: "auto", maxWidth: "700px", paddingBottom: "30px"
                        }}
                    />

                    <p>{t("guides.software.lib-ide-intro1")}</p>
                    <ul>
                        <li><b>Adafruit CCS811 Library</b>{" "}{t("guides.software.lib-by")}{" "}<b>Adafruit</b>{" "}{t("guides.software.lib-version")}{" "}<b>1.0.5</b></li>
                    </ul>

                    <h4>{t("guides.software.lib-zip-title")}</h4>
                    <p>
                        {t("guides.software.lib-zip-lmic0")}{" "}
                        <a href="https://github.com/matthijskooijman/arduino-lmic" target="_blank" rel="noopener noreferrer">{t("guides.software.lib-zip-lmic1")}</a>{" "}
                        {t("guides.software.lib-zip-lmic2")}
                    </p>

                    <h3>{t("guides.software.upload-title")}</h3>

                    {codeModification()}

                    <p>{t("guides.software.upload-board-uno")}</p>

                    <p>{t("guides.software.upload-connect-sn")}</p>
                    <p>{t("guides.software.upload-sn")}</p>

                    <img
                        src={serial}
                        alt="Serial monitor output example."
                        style={{
                            height: "auto", width: "100%", display: "block", marginLeft: "auto",
                            marginRight: "auto", maxWidth: "400px", paddingBottom: "30px"
                        }}
                    />

                    <p>{t("guides.software.outro-sn")}</p>
                    <Button className="float-right" color="danger" onClick={() => { toggle('3'); window.scrollTo(0, 0); }}>{t("guides.next-tab")}{t("guides.tab-use-sn")}</Button>
                </TabPane>
                <TabPane tabId="3">
                    <AlarmSNUse stackStatus={stack} />
                </TabPane>
            </TabContent>
        </div>
    );
};

export default AlarmSNGas;
