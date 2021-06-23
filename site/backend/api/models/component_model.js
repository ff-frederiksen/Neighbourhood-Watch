const ac = require("./atomic_component.js");
//CONTROL PANEL
const controlPanel = [
  {
    ac: ac.arduinoMega,
    amount: 1
  },
  {
    ac: ac.draginoLoraShield,
    amount: 1
  },
  {
    ac: ac.lcd,
    amount: 1
  },
  {
    ac: ac.keypad,
    amount: 1
  },
  {
    ac: ac.pushButton,
    amount: 1
  },
  {
    ac: ac.resistor,
    amount: 1
  },
  {
    ac: ac.breadboard,
    amount: 1
  },
  {
    ac: ac.cable,
    amount: 1
  },
  {
    ac: ac.powerSupply,
    amount: 1
  },
];

//SENSOR NODE
const sensorNode = [
  {
    ac: ac.arduinoUno,
    amount: 1
  },
  {
    ac: ac.draginoLoraShield,
    amount: 1
  },
  {
    ac: ac.cable,
    amount: 1
  },
  {
    ac: ac.powerSupply,
    amount: 1
  },
];
//CONTROL PANEL SETUPS
const cpPir = {
  name: "Control Panel - PIR",
  type: controlPanel,
  components: [
    {
      ac: ac.pir,
      amount: 1
    },
    {
      ac: ac.resistor,
      amount: 1
    },
    {
      ac: ac.mosfet,
      amount: 1
    },
    {
      ac: ac.maleToMaleWire,
      amount: 0.225
    },
    {
      ac: ac.maleToFemaleWire,
      amount: 0.1
    },
    {
      ac: ac.smallWire,
      amount: 0.029
    }
  ]
};
const cpLidar = {
  name: "Control Panel - LIDAR",
  type: controlPanel,
  components: [
    {
      ac: ac.lidar,
      amount: 1
    },
    {
      ac: ac.maleToMaleWire,
      amount: 0.125
    },
    {
      ac: ac.maleToFemaleWire,
      amount: 0.1
    },
    {
      ac: ac.smallWire,
      amount: 0.015
    }
  ]
};
const cpUs = {
  name: "Control Panel - Ultrasonic",
  type: controlPanel,
  components: [
    {
      ac: ac.usSensor,
      amount: 1
    },
    {
      ac: ac.maleToMaleWire,
      amount: 0.125
    },
    {
      ac: ac.maleToFemaleWire,
      amount: 0.1
    },
    {
      ac: ac.smallWire,
      amount: 0.036
    }
  ]
};
//SENSOR NODES SETUPS
const snPir = {
  name: "Sensor Node - PIR",
  type: sensorNode,
  components: [
    {
      ac: ac.pir,
      amount: 1
    },
    {
      ac: ac.breadboard,
      amount: 1
    },
    {
      ac: ac.mosfet,
      amount: 1
    },
    {
      ac: ac.resistor,
      amount: 1
    },
    {
      ac: ac.maleToMaleWire,
      amount: 0.15
    },
    {
      ac: ac.smallWire,
      amount: 0.015
    }
  ]
};
const snLidar = {
  name: "Sensor Node - LIDAR",
  type: sensorNode,
  components: [
    {
      ac: ac.lidar,
      amount: 1
    },
    {
      ac: ac.breadboard,
      amount: 1
    },
    {
      ac: ac.maleToMaleWire,
      amount: 0.05
    }
  ]
};
const snUs = {
  name: "Sensor Node - Ultrasonic",
  type: sensorNode,
  components: [
    {
      ac: ac.usSensor,
      amount: 1
    },
    {
      ac: ac.smallWire,
      amount: 0.022
    }
  ]
};

const snMikro = {
  name: "Sensor Node - Mikrobølge",
  type: sensorNode,
  components: [
    {
      ac: ac.mikro,
      amount: 1
    },
    {
      ac: ac.breadboard,
      amount: 1
    },
    {
      ac: ac.maleToMaleWire,
      amount: 0.125
    }
  ]
}
const snGas = {
  name: "Sensor Node - Gas",
  type: sensorNode,
  components: [
    {
      ac: ac.gas,
      amount: 1
    },
    {
      ac: ac.breadboard,
      amount: 1
    },
    {
      ac: ac.maleToMaleWire,
      amount: 0.125
    }
  ]
}
const snFugt = {
  name: "Sensor Node - Fugt",
  type: sensorNode,
  components: [
    {
      ac: ac.fugt,
      amount: 1
    },
    {
      ac: ac.breadboard,
      amount: 1
    },
    {
      ac: ac.maleToMaleWire,
      amount: 0.125
    }
  ]
}
const snHall = {
  name: "Sensor Node - Hall",
  type: sensorNode,
  components: [
    {
      ac: ac.hall,
      amount: 1
    },
    {
      ac: ac.breadboard,
      amount: 1
    },
    {
      ac: ac.maleToMaleWire,
      amount: 0.125
    }
  ]
}
exports.devices = [cpPir, cpLidar, cpUs, snPir, snLidar, snUs, snMikro, snGas, snFugt, snHall];
