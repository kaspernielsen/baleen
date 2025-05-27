import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout.component';
import { HomeComponent } from './home/home.component';
import { SubscribersComponent } from './subscribers/subscribers.component';
import { NiordComponent } from './niord/niord.component';
import { LoggingComponent } from './logging/logging.component';
import { AboutComponent } from './about/about.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'subscribers', component: SubscribersComponent },
      { path: 'niord', component: NiordComponent },
      { path: 'logging', component: LoggingComponent },
      { path: 'about', component: AboutComponent },
      { path: '', redirectTo: '/home', pathMatch: 'full' }
    ]
  }
];