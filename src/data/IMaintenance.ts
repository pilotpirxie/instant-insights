export interface IMaintenance {
  migrate(dir: string): Promise<void>;
  backup(): Promise<void>;
}
