import { Injectable } from '@angular/core';
import { CanDeactivate } from '@angular/router';
import { MenuComponent } from 'app/components/menu/menu.component';

@Injectable({
  providedIn: 'root'
})
export class CanDeactivateGuard implements CanDeactivate<MenuComponent> {
  canDeactivate(component: MenuComponent): boolean | Promise<boolean> {
    return component.confirmUnsavedChanges();
  }
}

