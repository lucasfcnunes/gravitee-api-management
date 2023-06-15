import { ADMIN_USER } from '@fakers/users/users';
import { ApiFakers } from '@fakers/apis';
import { Api } from '@model/apis';

import dashSidebar from "@pageobjects/DashBoard/DashSidebar"
import apiHome from "@pageobjects/Apis/ApiHome"

// This is purely for PageObject example and on hold for now until policy studio changes are completed.


    describe('Delete Flow feature', () => {
        beforeEach(() => {
          cy.loginInAPIM(ADMIN_USER.username, ADMIN_USER.password);
        });

        const DashSidebar = new dashSidebar()
        const ApiHome = new apiHome()

        it('Visit Home Page', () => {
            cy.visit('/')
            cy.wait(1000)
            cy.contains('Home board').should('be.visible');
          });

        it('Navigate to Test API', () => {
        // cy.visit('/#!/environments/DEFAULT/apis/');
        cy.visit('/')
        DashSidebar.apis().click();
        // above step = cy.get('.gio-menu-item__title').contains('APIs').click()
        ApiHome.addApi().click();
        // above step = cy.get('.mat-button-wrapper').contains('Add API').click()
      });
    });