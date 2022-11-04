export type Event = {
  id: string,
  appId: string,
  type: string,
  user: { [p: string]: string },
  params: { [p: string]: string },
  createdAt: string,
}
