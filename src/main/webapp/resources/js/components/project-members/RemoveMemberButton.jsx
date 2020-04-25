import React, { useContext, useState } from "react";
import { Button, notification, Popconfirm, Tooltip } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";
import { removeUserFromProject } from "../../apis/projects/members";
import { IconRemove } from "../icons/Icons";
import { PagedTableContext } from "../ant.design/PagedTable";

/**
 * React component to remove a member from a project
 * @param {object} user - details about the current user
 * @returns {*}
 * @constructor
 */
export function RemoveMemberButton({ user }) {
  const { updateTable } = useContext(PagedTableContext);
  const [loading, setLoading] = useState(false);

  /**
   * Handle the successful removal of the current user
   * @param message
   */
  const removeSuccess = (message) => {
    if (user.id !== window.PAGE.user) {
      notification.success({ message, className: "t-remove-success" });
      updateTable();
    } else {
      // If the user can remove themselves from the project, then when they
      // are removed redirect them to their project page since they cannot
      // use this project anymore.
      window.location.href = setBaseUrl(`/projects`);
    }
  };

  /**
   * Make the request to remove the user from the project.
   */
  const removeUser = () => {
    setLoading(true);
    removeUserFromProject(user.id)
      .then(removeSuccess)
      .catch((error) =>
        notification.error({
          message: error.response.data,
          className: "t-remove-error",
        })
      )
      .finally(() => setLoading(false));
  };

  return (
    <Popconfirm
      className="t-remove-popover"
      okButtonProps={{ className: "t-remove-confirm" }}
      onConfirm={removeUser}
      placement="topLeft"
      title={i18n("RemoveMemberButton.confirm")}
    >
      <Tooltip title={i18n("RemoveMemberButton.tooltip")} placement="left">
        <Button
          className="t-remove-member-btn"
          icon={<IconRemove />}
          shape="circle-outline"
          loading={loading}
        />
      </Tooltip>
    </Popconfirm>
  );
}
