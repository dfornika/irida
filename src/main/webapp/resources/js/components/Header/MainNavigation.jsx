import React from "react";
import { Icon, Menu } from "antd";

const { Divider, Item, ItemGroup, SubMenu } = Menu;

export function MainNavigation() {
  return (
    <Menu mode="horizontal">
      <Item key="home">
        <a href={window.TL.BASE_URL}>IRIDA LOGO HERE</a>
      </Item>
      <SubMenu
        title={
          <span className="submenu-title-wrapper">
            <Icon type="book" />
            Projects
          </span>
        }
      >
        <Item key="projects">
          <a href={`${window.TL.BASE_URL}projects`}>Your Projects</a>
        </Item>
        <Item key="project-new">
          <a href={`${window.TL.BASE_URL}projects/new`}>NEW PROJECT</a>
        </Item>
        <Item key="synchronize">
          <a href={`${window.TL.BASE_URL}projects/synchronize`}>SYNCHRONIZE</a>
        </Item>
        <ItemGroup title="ADMINISTRATOR">
          <Item key="projects-admin">
            <a href={`${window.TL.BASE_URL}projects/all`}>All Projects</a>
          </Item>
        </ItemGroup>
      </SubMenu>
    </Menu>
  );
}
