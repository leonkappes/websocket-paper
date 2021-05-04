<template>
  <v-list-item
    :key="title"
    :class="{ 'blue--text': current === index }"
    style="pointer-events: none"
    :style="getVars"
  >
    <v-list-item-content>
      <v-list-item-title
        style="color: white"
        v-text="title"
      ></v-list-item-title>
    </v-list-item-content>
    <div class="action">
      <v-list-item-action-text v-text="time"></v-list-item-action-text>

      <div>
        <div v-if="this.$store.state.privileged" style="display: unset">
          <v-icon
            v-if="current !== index"
            color="grey lighten-1"
            style="pointer-events: auto"
            @click="askConfirmation"
            >mdi-arrow-right-drop-circle-outline</v-icon
          >
          <v-icon v-else color="red darken-3"
            >mdi-arrow-right-drop-circle</v-icon
          >
        </div>
        <!-- IOS/Safari still doesnt support notifications after 5 years!!!!! Roadmap says will maybe be available in 2021 -->
        <!-- <v-icon
          v-if="!notification"
          color="grey lighten-1"
          style="pointer-events: auto"
          @click="toggleNotification"
        >
          mdi-alarm-off
        </v-icon>

        <v-icon
          v-else
          color="yellow darken-3"
          style="pointer-events: auto"
          @click="toggleNotification"
        >
          mdi-alarm
        </v-icon> -->
      </div>
    </div>
  </v-list-item>
</template>

<script>
import { mapState } from 'vuex'

export default {
  props: {
    title: {
      type: String,
      required: true,
    },
    time: {
      type: String,
      required: true,
    },
    index: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      notification: false,
    }
  },
  computed: {
    getVars() {
      return {
        '--conf-opacity': this.current === this.index ? '0.24' : '0',
        '--bgColor':
          this.current === this.index ? 'currentColor' : 'transparent',
      }
    },
    ...mapState(['current']),
  },
  mounted() {
    this.unsubscribe = this.$store.subscribe((mutation, state) => {
      if (mutation.type === 'setCurrent') {
        if (state.current === this.index) {
          setTimeout(() => {
            this.$el.classList.add('v-list-item--active')
          }, 300)
        }
      }
    })
  },
  beforeDestroy() {
    this.unsubscribe()
  },
  methods: {
    /* toggleNotification() {
      this.notification = !this.notification
      setTimeout(() => {
        this.$el.classList.add('v-list-item--active')
      }, 300)
    }, */
    // Method triggered when you want to change the current active element
    askConfirmation() {
      if (this.current !== this.index) {
        // Send an event to the index page telling it to show the confirmation dialog
        this.$root.$emit('askConfirmation', this.index)
      }
      // Readd active class which sometimes gets removed when clicking buttons(feature?!? of the ui-framework)
      setTimeout(() => {
        this.$el.classList.add('v-list-item--active')
      }, 300)
    },
  },
}
</script>

<style scoped>
.v-list-item--active::before {
  opacity: var(--conf-opacity) !important;
  background-color: var(--bgColor) !important;
}
.action {
  display: inline-flex;
  min-width: 24px;
  margin: 12px 0;
  align-items: flex-end;
  align-self: stretch;
  justify-content: space-between;
  white-space: nowrap;
  flex-direction: column;
}
</style>
