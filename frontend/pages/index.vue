<template>
  <v-list>
    <!-- Delay Header, only show if we have an active conference -->
    <v-subheader v-if="items.length > 0"
      >Verzug: <span :class="textColor"> &nbsp;{{ delay }}m</span></v-subheader
    >
    <!-- Create the create and join buttons, also only shown when no active conference -->
    <v-list-item v-if="items.length == 0" @click="$router.push('settings')">
      <v-list-item-content>
        <v-list-item-title>Konferenz einrichten</v-list-item-title>
        <v-list-item-subtitle
          >Erstelle eine neue Konferenz
        </v-list-item-subtitle>
      </v-list-item-content>
    </v-list-item>
    <v-list-item v-if="items.length == 0" @click="dialog2 = true">
      <v-list-item-content>
        <v-list-item-title>Konferenz beitreten</v-list-item-title>
        <v-list-item-subtitle
          >Einer vorhanden Konfernez beitreten
        </v-list-item-subtitle>
      </v-list-item-content>
    </v-list-item>
    <!-- Actual Conference -->
    <v-list-item-group v-model="current" active-class="" multiple>
      <!-- Create the conference-items in an loop -->
      <template v-for="(item, index) in items">
        <conference-item
          :key="item.description"
          :title="item.description"
          :time="item.start"
          :index="index"
        ></conference-item>
        <v-divider v-if="index < items.length - 1" :key="index"></v-divider>
      </template>
    </v-list-item-group>
    <!-- Dialogs -->
    <!-- Dialog show when your about to switch the conference item, show consequenzes -->
    <v-dialog v-model="dialog" max-width="290">
      <v-card>
        <v-card-title class="headline">
          Konferenz wirklich wechseln?
        </v-card-title>

        <v-card-text>
          Ein Wechsel wird sofort bei allen verbundenen Teilnehmern angezeigt
          und löst zudem, bei Teilnehmern die dies aktiviert haben, eine
          Benachrichtigung aus.
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn color="green darken-1" text @click="dialog = false">
            Abbrechen
          </v-btn>

          <v-btn color="green darken-1" text @click="accept"> Wechseln </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- Dialog asking you for the conference-id when your joining an active conference -->
    <v-dialog v-model="dialog2" max-width="290">
      <v-card>
        <v-card-title class="headline"> Konferenz beitreten </v-card-title>

        <v-card-text>
          Bitte den Beitritscode angeben. Diesen erhalten sie vom
          Konferenzleiter
          <v-text-field
            v-model="input"
            label="Konferenzcode"
            single-line
          ></v-text-field>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn color="green darken-1" text @click="dialog2 = false">
            Abbrechen
          </v-btn>

          <v-btn color="green darken-1" text @click="join"> Beitreten </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-list>
</template>

<script>
import { mapState } from 'vuex'
import ConferenceItem from '~/components/ConferenceItem.vue'

export default {
  components: {
    ConferenceItem,
  },
  data() {
    return {
      current: [0],
      dialog: false,
      dialog2: false,
      input: '',
      dialogIndex: 0,
    }
  },
  computed: {
    textColor() {
      // This methods decides in which color the delay is shown, when delay is greater then 0 then it is red else it is green
      return this.delay === 0
        ? ''
        : this.delay < 0
        ? 'green--text'
        : 'red--text'
    },
    ...mapState(['items', 'delay']),
  },
  mounted() {
    // Add a listener to a function, which saves everything when you close a tab
    window.addEventListener('beforeunload', this.handler)
    // Event which gets triggerd by a conferenceitem when you pressed the set as active button, initiates the confirmation dialog
    this.$root.$on('askConfirmation', (index) => {
      this.dialog = true
      this.dialogIndex = index
    })
    // Websocket listener, triggered when the conference gets updated
    this.$websocket.on('updateConference', (data) => {
      // data is a string, parse it to an object
      const json = JSON.parse(data)
      // Set the items and delay in storage, triggers an automatic update in the ui
      this.$store.commit('setItems', json.conference.conferenceItems)
      this.$store.commit('setDelay', json.conference.delay)
      // Loop through the items and set the active one in the ui
      for (let index = 0; index < this.items.length; index++) {
        const element = this.items[index]
        if (element.description === json.conference.currentItem.description) {
          this.$store.commit('setCurrent', index)
        }
      }
      // Save everything to localstorage in the event the application gets closed
      localStorage.setItem('code', this.$store.state.code)
      localStorage.setItem('items', JSON.stringify(this.$store.state.items))
    })
    // Code which runs when the app gets opened
    // Load the conference out of storage, if available
    if (
      this.items.length === 0 &&
      localStorage.getItem('code') !== null &&
      localStorage.getItem('code') !== ''
    ) {
      this.$store.commit('setCode', localStorage.getItem('code'))
      this.$store.commit(
        'setPrivileged',
        localStorage.getItem('privileged') === 'true'
      )
      this.$store.commit(
        'setCurrent',
        parseInt(localStorage.getItem('current'))
      )
      this.$store.commit('setDelay', parseInt(localStorage.getItem('delay')))
      this.$store.commit('setItems', JSON.parse(localStorage.getItem('items')))
      // Login again, so the server knows we want future update
      this.$websocket.send(
        'login',
        `{ "privileged": ${this.$store.state.privileged}, "code": "${this.$store.state.code}" }`
      )
    }
  },
  beforeDestroy() {
    // before unloading this component, unregister the listener, so we dont save incorrect data
    window.removeEventListener('beforeunload', this.handler)
  },
  methods: {
    // Method responsible for saving everything in case of app close
    handler(event) {
      event.preventDefault()
      localStorage.setItem('code', this.$store.state.code)
      localStorage.setItem('privileged', this.$store.state.privileged)
      localStorage.setItem('current', this.$store.state.current)
      localStorage.setItem('delay', this.$store.state.delay)
      localStorage.setItem('items', JSON.stringify(this.$store.state.items))
    },
    // Method triggered if the switch dialog was confirmed
    accept() {
      // Set new current item
      this.$store.commit('setCurrent', this.dialogIndex)
      // Send switch to server so all clients get the update
      this.$websocket.send(
        'controlConference',
        `{"switchTo": ${this.dialogIndex}, "code": "${this.$store.state.code}"}`
      )
      this.dialog = false
    },
    // Method triggered if you join a conference
    join() {
      // Set the conference code your now connected to
      this.$store.commit('setCode', this.input)
      this.input = ''
      // Login with the server to receive the conference information
      this.$websocket.send(
        'login',
        `{ "privileged": false, "code": "${this.$store.state.code}" }`
      )
      this.dialog2 = false
    },
  },
}
</script>

<style></style>
