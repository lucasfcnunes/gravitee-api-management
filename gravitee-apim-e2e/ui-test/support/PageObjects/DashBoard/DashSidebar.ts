
class dashSidebar {

    dashboard(){return cy.get('.gio-menu-item__title').contains('Dashboard')}

    apis(){return cy.get('.gio-menu-item__title').contains('APIs')}

    applications(){return cy.get('.gio-menu-item__title').contains ('Applications')}

}

export default dashSidebar;