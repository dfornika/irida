import React, { Suspense } from "react";
import { render } from "react-dom";
import { blue1 } from "../../styles/colors";
import { MainNavigation } from "./MainNavigation";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = `${window.TL.BASE_URL}dist/`;

const GalaxyAlert = React.lazy(() => import("./GalaxyAlert"));

export class PageHeader extends React.Component {
  state = {
    inGalaxy: false
  };

  componentDidMount() {
    if (typeof window.GALAXY !== "undefined") {
      this.setState({ inGalaxy: true });
    }
  }

  render() {
    return (
      <>
        <MainNavigation/>
        {this.state.inGalaxy ? (
          <Suspense
            fallback={
              <div
                style={{
                  backgroundColor: blue1,
                  height: 58
                }}
              />
            }
          >
            <GalaxyAlert />
          </Suspense>
        ) : null}
      </>
    );
  }
}

render(<PageHeader />, document.querySelector(".js-page-header"));
