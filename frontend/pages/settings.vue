<template>
  <v-list>
    <!-- Button for setting up a conference -->
    <v-list-item v-if="!!!code" @click="toggleUpload">
      <v-list-item-content>
        <v-list-item-title>Konferenzplan setzen</v-list-item-title>
        <v-list-item-subtitle
          >Erfordert eine CSV-Datei, nach folgenden Format:<br />
          Beschreibung (z.B. „9e“ oder „Pause“), Start (z.B. „9:15 Uhr“)
          <br />
          und Dauer in Minuten (z.B. „25“)
        </v-list-item-subtitle>
      </v-list-item-content>
    </v-list-item>
    <!-- Button displaying the conference-code, copy to clipboard on press -->
    <v-list-item v-if="!!code" @click="copy">
      <v-list-item-content>
        <v-list-item-title
          >Konferenzcode:
          <span class="font-weight-black">{{ code }}</span></v-list-item-title
        >
      </v-list-item-content>
    </v-list-item>
    <!-- Dialog for uploading a .csv sheet -->
    <v-dialog v-model="dialog" max-width="290">
      <v-card>
        <v-card-title class="headline"> Datei hochladen </v-card-title>

        <v-card-text>
          <v-container>
            <v-file-input
              accept=".csv"
              label="CSV-Tabelle"
              @change="selectFile"
            ></v-file-input>
          </v-container>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="dialog = false">
            Abbrechen
          </v-btn>
          <v-btn color="blue darken-1" text @click="sendUpload">
            Speichern
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- Dialog showing the code after creation -->
    <v-dialog v-model="dialog2" max-width="290">
      <v-card>
        <v-card-title class="headline"> Konferenz erstellt </v-card-title>

        <v-card-text>
          <v-container>
            Der Konferenz code ist:
            <span class="font-weight-black">{{ code }}</span>
          </v-container>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" text @click="dialog2 = false">
            Schließen
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- Notification when copying the code -->
    <v-snackbar v-model="snackbar" timeout="2000">
      Code kopiert!
      <template #action="{ attrs }">
        <v-btn color="blue" text v-bind="attrs" @click="snackbar = false">
          Schließen
        </v-btn>
      </template>
    </v-snackbar>
    <!-- Notification when wrong format -->
    <v-snackbar v-model="snackbar2" timeout="2000">
      Das Format der CSV-Datei ist falsch!
      <template #action="{ attrs }">
        <v-btn color="blue" text v-bind="attrs" @click="snackbar2 = false">
          Schließen
        </v-btn>
      </template>
    </v-snackbar>
  </v-list>
</template>

<script>
import { mapState } from 'vuex'
import * as Papa from 'papaparse'

export default {
  data() {
    return {
      dialog: false,
      dialog2: false,
      file: undefined,
      snackbar: false,
      snackbar2: false,
    }
  },
  computed: mapState(['code']),
  methods: {
    // Shows the upload dialog
    toggleUpload() {
      this.dialog = true
    },
    // Sets the uploaded file as an attribute to this object
    selectFile(file) {
      this.file = file
    },
    // Copys the code to the clipboard, displays notification if successfull
    copy() {
      navigator.clipboard.writeText(this.code).then(() => {
        this.snackbar = true
      })
    },
    // Method responsible for parsing the sheet and sending the conference to the server
    sendUpload() {
      // Read the file contents
      const reader = new FileReader()
      reader.readAsText(this.file)
      reader.onloadend = () => {
        // Parse the csv to a js-object
        Papa.parse(reader.result, {
          complete: (result) => {
            // Check if we have a result
            if (result.data[0]) {
              // Only run code if we have all required headers
              if (
                result.data[0].Beschreibung &&
                result.data[0].Dauer &&
                result.data[0].Start
              ) {
                // Generate a new random code based on time and a random number
                const code = (+new Date() + Math.random())
                  .toString(36)
                  .slice(-7)
                  .replace('.', '')
                // Send the newly parsed conference and the generated code to the server
                this.$websocket.send(
                  'setupConference',
                  `{"conference": ${JSON.stringify(
                    result.data
                  )}, "code": "${code}" }`
                )
                // Login to the server as the conference host
                this.$websocket.send(
                  'login',
                  `{ "privileged": true, "code": "${code}" }`
                )
                // Set everything in the app context
                this.$store.commit('setCode', code)
                this.$store.commit('setPrivileged', true)
                // Show code to user
                this.dialog2 = true
              } else {
                this.snackbar2 = true
              }
            } else {
              this.snackbar2 = true
            }
          },
          header: true,
        })
      }
      // reset all attributes
      this.dialog = false
      this.file = undefined
    },
  },
}
</script>
<style></style>