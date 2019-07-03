import React from "react";
import { Badge, Icon, Input, Menu } from "antd";

const { Divider, Item, ItemGroup, SubMenu } = Menu;
const { Search } = Input;

export function MainNavigation() {
  return (
    <Menu mode="horizontal" style={{ display: "flex" }}>
      <Item key="home">
        <a href={window.TL.BASE_URL}>
          <img style={{height: 30}} src="/resources/img/irida_logo.svg" alt=""/>
        </a>
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
          <a href={`${window.TL.BASE_URL}projects/new`}>New Project</a>
        </Item>
        <Item key="synchronize">
          <a href={`${window.TL.BASE_URL}projects/synchronize`}>
            Synchronize Remote Projects
          </a>
        </Item>
        <ItemGroup title="ADMINISTRATOR">
          <Item key="projects-admin">
            <a href={`${window.TL.BASE_URL}projects/all`}>All Projects</a>
          </Item>
        </ItemGroup>
      </SubMenu>
      <SubMenu
        title={
          <span className="submenu-title-wrapper">
            <Icon type="area-chart" />
            Analyses
          </span>
        }
      >
        <Item key="analyses">
          <a href={`${window.TL.BASE_URL}analysis`}>Your Analyses</a>
        </Item>
        <Item key="projects-admin">
          <a href={`${window.TL.BASE_URL}analysis/user/analysis-outputs`}>
            Output Files
          </a>
        </Item>
        <ItemGroup title="ADMINISTRATOR">
          <Item key="analysis-admin">
            <a href={`${window.TL.BASE_URL}analysis/all`}>All Analyses</a>
          </Item>
        </ItemGroup>
      </SubMenu>
      <Item key="search" disabled={true} style={{ flex: 1 }}>
        <Search placeholder="Search" style={{ width: "100%" }} />
      </Item>
      <SubMenu title={<Icon type="question-circle" />}>
        <Item key="guide">
          <a href="#">User Guide</a>
        </Item>
        <Item key="admin-guid">
          <a href="#">Admin Guide</a>
        </Item>
        <Item key="biohub">
          <a href="#">BioHUB IRIDA</a>
        </Item>
        <Divider />
        <Item key="contact">
          <a href="#">Contact Us</a>
        </Item>
        <Item key="website">
          <a href="https://www.irida.ca">IRIDA Website</a>
        </Item>
        <Divider />
        <Item key="version">
          <span>irida-19.05.1</span>
        </Item>
      </SubMenu>
      <Item key="cart">
        <Badge count={99}>
          <a href={`${window.TL.BASE_URL}cart`}>
            <Icon type="shopping-cart"/>
          </a>
        </Badge>
      </Item>
      <SubMenu title={<Icon type="setting" />}>
        <Item key="users">
          <a href="#">Users</a>
        </Item>
      </SubMenu>
      <SubMenu title={<Icon type="user" />}>
        <Item key="account">
          <a href="#">Account</a>
        </Item>
        <Item key="logout">
          <a href={`${window.TL.BASE_URL}logout`}>Logout</a>
        </Item>
      </SubMenu>
    </Menu>
  );
}
