/**
 *  Copyright 2020 Brad Sileo
 *
 *  Intellibrite Color Mode Device
 *
 *  Author: Brad Sileo
 *
 */
metadata {
	definition (name: "Pentair Chlorinator", 
            namespace: "bsileo", 
            author: "Brad Sileo",
            importUrl: 'https://raw.githubusercontent.com/bsileo/hubitat_poolcontroller/master/pentair_chlorinator.groovy') {
		capability "Refresh"
        capability "Switch"
		attribute "saltPPM", "string"
		attribute "currentOutput", "string"
		attribute "superChlorinate", "string"
		attribute "status", "string"
		attribute "poolSpaSetpoint", "string"        
	}

	tiles (scale:2) {
		standardTile("chlorinate", "device.switch", width:1, height:1, inactiveLabel: false, decoration: "flat") {
			state "off",  action:"on", nextState: "updating", icon: "https://bsileo.github.io/SmartThings_Pentair/chlorine.png",backgroundColor: "#ffffff"
			state "on", action:"off",  nextState: "updating", icon: "https://bsileo.github.io/SmartThings_Pentair/chlorine.png",backgroundColor: "#00a0dc"
			state "updating", label:"Updating...", icon: "st.secondary.secondary"
		}
        
        valueTile("saltPPM", "saltPPM",width:1, height:1, decoration:"flat") {
            state("saltPPM", label:'${currentValue}',
				backgroundColors:[							
							[value: 0, color: "#153591"],
							[value: 2400, color: "#44b621"],
							[value: 2800, color: "#f1d801"],
							[value: 3000, color: "#d04e00"],
							[value: 3200, color: "#bc2323"]
                     ]
				)
			}        
		valueTile("currentOutput","currentOutput",width:1, height:1, decoration:"flat")  {
        	state("currentOutput", label:'${currentValue}%') 
        }
        
        valueTile("poolSpaSetpoint","poolSpaSetpoint",width:1, height:1, decoration:"flat")  {
        	state("poolSpaSetpoint", label:'${currentValue}') 
        }
        
        valueTile("superChlorinate","superChlorinate",width:1, height:1, decoration:"flat")  {
        	state("superChlorinate", label:'${currentValue}') 
        }
        
        valueTile("status","status",width:2, height:1, decoration:"flat")  {
        	state("status", label:'${currentValue}') 
        }
        
        
	    standardTile("refresh", "device.thermostatMode", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		
	}
}

def installed() {

}

def updated() {
    if (!state.updatedLastRanAt || now() >= state.updatedLastRanAt + 5000) {
		state.updatedLastRanAt = now()
		log.debug "Executing 'updated'"
        initialize()
	}
	else {
		log.trace "updated(): Ran within last 5 seconds so aborting."
	}
}

def initialize() {

}

def parse(json)
{
	def name;
    def val;
	json.each { k, v ->      	 
         name = k;
         val = v;
         switch (k) {
        	case "outputPoolPercent":
            	def sp = json.outputSpaPercent + "%"
            	val = "${v}% / " + sp
            	name = "poolSpaSetpoint"
            break; 
            case "superChlorinate":
            	val = v ? "Yes": "No"
            break
            case "currentOutput":
                def sw = v==0 ? "off":"on"
                log.debug("SWITCH ${sw}")
            	sendEvent(name: "switch", value: sw,displayed:true ) 
                break;
        }
        //log.debug("EVT for ${k}->${v} == [${name}] -> {$val})")
        sendEvent(name: name, value: val, displayed:true) 
	}
    
}



// Command Implementations
def poll() {
	refresh()
}

def refresh() {
	pollDevice()
}

def pollDevice() {
    parent.poll()
}

def on() {
	parent.chlorinatorOn()
}

def off() {
    parent.chlorinatorOff()
}
