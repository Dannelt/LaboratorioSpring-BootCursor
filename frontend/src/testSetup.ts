// jsdom does not implement the native dialog lifecycle. Browser behavior is retained in production.
Object.defineProperty(HTMLDialogElement.prototype, 'showModal', {
  configurable: true,
  value: function (this: HTMLDialogElement) { this.setAttribute('open', '') },
})
Object.defineProperty(HTMLDialogElement.prototype, 'close', {
  configurable: true,
  value: function (this: HTMLDialogElement) { this.removeAttribute('open') },
})
