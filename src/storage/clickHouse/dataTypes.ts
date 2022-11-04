export type EventEntity = {
  id?: string,
  app_id: string,
  type: string,
  user: { [p: string]: string },
  params: { [p: string]: string },
  created_at?: string,
}
