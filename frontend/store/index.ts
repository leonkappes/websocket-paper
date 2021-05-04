// Current state/context of the application
export const state = () => ({
  privileged: false,
  // notificationOn: [],
  code: '',
  items: [],
  delay: 0,
  current: 0,
})

// Setters for the state/context. Called with this.$store.commit("setterName", params...)
export const mutations = {
  setPrivileged(state: { privileged: boolean }, newValue: boolean) {
    state.privileged = newValue
  },
  /* addNotification(state: { notificationOn: number[] }, index: number) {
    state.notificationOn.push(index)
  }, */
  setCode(state: { code: string }, code: string) {
    state.code = code
  },
  setItems(state: { items: string[] }, items: string[]) {
    state.items = items
  },
  setDelay(state: { delay: number }, delay: number) {
    state.delay = delay
  },
  setCurrent(state: { current: number }, current: number) {
    state.current = current
  },
}
