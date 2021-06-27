import React from "react";
import { Table } from "reactstrap";
import { useTranslation } from "react-i18next";
import { Link, withRouter } from "react-router-dom";
import DeviceForm from "./DeviceForm";
import ToolDescription from "./ToolDescription.js";
import exampleImage from "../Images/alarmDesign-example.png";

const AlarmDesign = props => {
  var stack = props.stackStatus;
  console.log(stack);

  const { t } = useTranslation("alarm_v1-" + stack);

  return (
    <div>
      <h1>
        {t("navigation.design")}<span style={{ color: "grey", fontSize: "40%" }}>{t("design.version")}</span>
      </h1>
      <p>
        {t("design.devicetypes0")}
        <ToolDescription id="arm-design" name={t("tooltip.armname")} description={t("tooltip.armdesc")} />
        {t("design.devicetypes1")}
      </p>
      <p>
        {t("design.sensors0")}
        <ToolDescription id="lidar-design" name={t("tooltip.lidarname")} description={t("tooltip.lidardesc")} />
        {t("design.sensors1")}
        <ToolDescription id="pir-design" name={t("tooltip.pirname")} description={t("tooltip.pirdesc")} />
        {t("design.sensors1")}
        <ToolDescription id="ultra-design" name={t("tooltip.ultraname")} description={t("tooltip.ultradesc")} />
        {t("design.sensors1")}
        <ToolDescription id="mikro-design" name={t("tooltip.mikroname")} description={t("tooltip.mikrodesc")} />
        {t("design.sensors1")}
        <ToolDescription id="gas-design" name={t("tooltip.gasname")} description={t("tooltip.gasdesc")} />
        {t("design.sensors1")}
        <ToolDescription id="fugt-design" name={t("tooltip.fugtname")} description={t("tooltip.fugtdesc")} />
        {t("design.sensors2")}
        <ToolDescription id="hall-design" name={t("tooltip.hallname")} description={t("tooltip.halldesc")} />
        {t("design.sensors3")}
      </p>
      <Table bordered striped>
        <thead>
          <tr>
            <th>{t("design.sensortable.type")}</th>
            <th>{t("design.sensortable.model")}</th>
            <th>{t("design.sensortable.range")}</th>
            <th>{t("design.sensortable.angle")}</th>
            <th>{t("design.sensortable.price")}</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>{t("design.sensortable.lidartype")}</td>
            <td>{t("design.sensortable.lidarmodel")}</td>
            <td>{t("design.sensortable.lidarrange")}</td>
            <td>{t("design.sensortable.lidarangle")}</td>
            <td>{t("design.sensortable.lidarprice")}</td>
          </tr>
          <tr>
            <td>{t("design.sensortable.pirtype")}</td>
            <td>{t("design.sensortable.pirmodel")}</td>
            <td>{t("design.sensortable.pirrange")}</td>
            <td>&lt;100&deg;</td>
            <td>{t("design.sensortable.pirprice")}</td>
          </tr>
          <tr>
            <td>{t("design.sensortable.ustype")}</td>
            <td>{t("design.sensortable.usmodel")}</td>
            <td>{t("design.sensortable.usrange")}</td>
            <td>62&deg;</td>
            <td>{t("design.sensortable.usprice")}</td>
          </tr>
          <tr>
            <td>{t("design.sensortable.mikrotype")}</td>
            <td>{t("design.sensortable.mikromodel")}</td>
            <td>{t("design.sensortable.mikrorange")}</td>
            <td>360&deg;</td>
            <td>{t("design.sensortable.mikroprice")}</td>
          </tr>
          <tr>
            <td>{t("design.sensortable.gastype")}</td>
            <td>{t("design.sensortable.gasmodel")}</td>
            <td>{t("design.sensortable.gasrange")}</td>
            <td>360&deg;</td>
            <td>{t("design.sensortable.gasprice")}</td>
          </tr>
          <tr>
            <td>{t("design.sensortable.fugttype")}</td>
            <td>{t("design.sensortable.fugtmodel")}</td>
            <td>{t("design.sensortable.fugtrange")}</td>
            <td>360&deg;</td>
            <td>{t("design.sensortable.fugtprice")}</td>
          </tr>
          <tr>
            <td>{t("design.sensortable.halltype")}</td>
            <td>{t("design.sensortable.hallmodel")}</td>
            <td>{t("design.sensortable.hallrange")}</td>
            <td>-&deg;</td>
            <td>{t("design.sensortable.hallprice")}</td>
          </tr>
        </tbody>
      </Table>
      <p>
        {t("design.housedesign0")}{" "}
        <span style={{ color: "red" }}>{t("design.housedesign1")}</span>
        {t("design.housedesign2")}{" "}
        <span style={{ color: "blue" }}>{t("design.housedesign3")}</span>
        {t("design.housedesign4")}{" "}
        <span style={{ color: "limegreen" }}>{t("design.housedesign5")}</span>
        {" "}{t("design.housedesign6")}
      </p>
      <p>
        <img
          className="photo-logo"
          src={exampleImage}
          alt="nwa system"
          style={{ paddingRight: "50px", height: "auto", maxWidth: "100%" }}
        />
      </p>
      <p>{t("design.deviceformintro")}</p>
      <DeviceForm />
      <p>{t("design.outro0")}{" "}
        <Link to="/server" className="alert-link">
          {t("design.outro1")}
        </Link>
        {t("design.outro2")}
      </p>
    </div>
  );
};

export default withRouter(AlarmDesign);
