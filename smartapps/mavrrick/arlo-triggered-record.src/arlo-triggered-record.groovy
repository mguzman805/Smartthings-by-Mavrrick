/**
 *  Arlo Triggered Record
 *
 *  Copyright 2018 Mavrrick
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Arlo Triggered Record",
    namespace: "Mavrrick",
    author: "Mavrrick",
    description: "Trigger a recording based on device action.",
    category: "My Apps",
	iconUrl: "https://storage.googleapis.com/arlopilot/arlo-small.png",
	iconX2Url: "https://storage.googleapis.com/arlopilot/arlo-med.png",
	iconX3Url: "https://storage.googleapis.com/arlopilot/arlo-large.png"
    )

import groovy.time.TimeCategory 

preferences {
	section("When any of the following devices trigger..."){
		input "motion", "capability.motionSensor", title: "Motion Sensor?", required: false
		input "contact", "capability.contactSensor", title: "Contact Sensor?", required: false
        input "myButton", "capability.momentary", title: "What Button?", required: false, multiple: false
		input "acceleration", "capability.accelerationSensor", title: "Acceleration Sensor?", required: false
		input "mySwitch", "capability.switch", title: "Switch?", required: false
		input "myPresence", "capability.presenceSensor", title: "Presence Sensor?", required: false
        input "myMoisture", "capability.waterSensor", title: "Moisture Sensor?", required: false
	}
	section("Setup") {
		input "cameras", "capability.videoCapture", description: "Please select the cameras will when the triggered", multiple: true
        input name: "clipLength", type: "number", title: "Clip Length", description: "Please enter the length of each recording", required: true, range: "5..300"
        }
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	if (contact) {
		subscribe(contact, "contact.open", arloCapture)
	}
	if (acceleration) {
		subscribe(acceleration, "acceleration.active", arloCapture)
	}
	if (motion) {
		subscribe(motion, "motion.active", arloCapture)
	}
	if (mySwitch) {
		subscribe(mySwitch, "switch.on", arloCapture)
	}
	if (myPresence) {
		subscribe(myPresence, "presence", arloCapture)
	}
    if (myMoisture) {
    	subscribe(myMoisture, "water.wet", arloCapture)
        }
    if (myButton) {    
        subscribe(myButton, "momentary.pushed", arloCapture)
		}
}

def arloCapture(evt) {	
	log.debug "$evt.name: $evt.value"
	log.debug "Refreshing cameras with ${clipLength} second capture"
    Date start = new Date()
    Date end = new Date()
    use( TimeCategory ) {
    	end = start + clipLength.seconds
 	}
    log.debug "Capturing..."
    cameras.capture(start, start, end)
}