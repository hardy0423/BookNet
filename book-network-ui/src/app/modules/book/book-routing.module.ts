import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {Main} from './pages/main/main';
import {authGuard} from '../../services/guard/guard';
import {BookList} from './pages/book-list/book-list';
import {MyBook} from './pages/my-book/my-book';

const routes: Routes = [
   {
     path: '',
     component: Main,
     canActivate: [authGuard],
     children: [
           {
             path: '',
             component: BookList,
             canActivate: [authGuard]
           },
         {
           path: 'my-books',
           component: MyBook,
           canActivate: [authGuard]
         },
     ]
   }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BookRoutingModule {
}
