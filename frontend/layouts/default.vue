<template>
  <v-app dark>
    <!-- navigation -->
    <v-navigation-drawer v-model="drawer" temporary absolute>
      <v-list dense>
        <!-- Main page with current conference and join button | see pages/index.vue -->
        <v-list-item key="Zeitplan" link nuxt to="/">
          <v-list-item-icon>
            <v-icon>mdi-view-dashboard</v-icon>
          </v-list-item-icon>

          <v-list-item-content>
            <v-list-item-title>Zeitplan</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        <!-- settings page, used for settings up a conference | see pages/settings.vue -->
        <v-list-item
          v-if="privileged"
          key="Konferenz verwalten"
          link
          nuxt
          to="settings"
        >
          <v-list-item-icon>
            <v-icon>mdi-forum</v-icon>
          </v-list-item-icon>

          <v-list-item-content>
            <v-list-item-title>Konferenz verwalten</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        <!-- leave button, see method leave below -->
        <v-list-item v-if="!!code" key="Konferenz verlassen" @click="leave">
          <v-list-item-icon>
            <v-icon>mdi-close</v-icon>
          </v-list-item-icon>

          <v-list-item-content>
            <v-list-item-title>Konferenz verlassen</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <v-main>
      <!-- Main header on all pages, has navigation button -->
      <v-container>
        <v-card class="mx-auto">
          <v-toolbar color="blue" dark>
            <v-app-bar-nav-icon
              @click.stop="drawer = !drawer"
            ></v-app-bar-nav-icon>
            <v-toolbar-title>Konferenztool</v-toolbar-title>
            <v-spacer></v-spacer>
          </v-toolbar>
          <!-- in runtime this element will be replaced with the content of the current page | see pages directory -->
          <nuxt />
        </v-card>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import { mapState } from 'vuex'

export default {
  data() {
    return {
      drawer: null,
    }
  },
  computed: mapState(['privileged', 'code']),
  methods: {
    // Method which gets triggered when you leave a conference, deletes everything out of storage and out of the context
    leave() {
      this.drawer = false
      this.$store.commit('setCode', '')
      this.$store.commit('setPrivileged', false)
      this.$store.commit('setCurrent', 0)
      this.$store.commit('setItems', [])
      this.$store.commit('setDelay', 0)
      localStorage.removeItem('code')
      localStorage.removeItem('privileged')
      localStorage.removeItem('items')
      localStorage.removeItem('current')
      localStorage.removeItem('delay')
      this.$router.push('/')
    },
  },
}
</script>

<style></style>
