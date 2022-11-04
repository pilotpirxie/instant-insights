export type AppEntity = {
  id: number,
  name: string,
  created_at?: string,
  updated_at?: string,
}

export type EventEntity = {
  app_id: number,
  type: string,
  meta: { [p: string]: string },
  params: { [p: string]: string },
  created_at?: string,
}
