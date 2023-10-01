package com.example.application.views.main;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
@Route("")
public class MenuBar extends AppLayout {

    public MenuBar() {
        H1 title = new H1("Pesquisa de livros virtual");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("left", "var(--lumo-space-l)").set("margin", "0")
                .set("position", "absolute");

        Tabs tabs = getTabs();
        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            if (selectedTab != null) {
                String tabName = selectedTab.getLabel();
                if (tabName.equals("Dashboard")) {
                    if (getContent()!=null){
                        getContent().removeFromParent();
                        setContent(null);
                    }else {
                        setContent(null);
                    }
                } else if (tabName.equals("Livros")) {
                    if (getContent()!=null){
                        getContent().removeFromParent();
                        setContent(new ListagemDeLivro());
                    }else {
                        setContent(new ListagemDeLivro());
                    }
                } else if (tabName.equals("Livros proibidos")) {
                    if (getContent()!=null) {
                        setContent(new ListagemDeLivroProibido());
                    } else {
                        setContent(new ListagemDeLivroProibido());
                    }
                }
            }
        });

        addToNavbar(title, tabs);
    }

    private Tabs getTabs() {
        Tabs tabs = new Tabs();
        tabs.getStyle().set("margin", "auto");
        tabs.add(createTab("Dashboard"),
                createTab("Livros"), createTab("Livros proibidos"));
        return tabs;
    }

    private Tab createTab(String viewName) {
        RouterLink link = new RouterLink();
        link.add(viewName);

        link.setTabIndex(-1);
        Tab tab = new Tab(link);
        tab.setLabel(viewName);

        return tab;
    }
}
