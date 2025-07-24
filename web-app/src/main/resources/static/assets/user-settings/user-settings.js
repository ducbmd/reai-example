let deleteKeyId = null;

function showCreateModal() {
  document.getElementById("create-modal").classList.remove("hidden");
}

function hideCreateModal() {
  document.getElementById("create-modal").classList.add("hidden");
  document.getElementById("create-api-key-form").reset();
}

function hideShowKeyModal() {
  document.getElementById("show-key-modal").classList.add("hidden");
  document.getElementById("new-api-key-display").innerHTML = "";
  document.body.dispatchEvent(new CustomEvent("apiKeyCreated"));
}

function showDeleteModal(keyId, keyName) {
  deleteKeyId = keyId;
  document.getElementById("delete-key-name").textContent = `Key: ${keyName}`;
  document.getElementById("delete-modal").classList.remove("hidden");
}

function hideDeleteModal() {
  document.getElementById("delete-modal").classList.add("hidden");
  deleteKeyId = null;
}

function confirmDelete() {
  if (deleteKeyId) {
    htmx
      .ajax("DELETE", `/user-settings/api-keys/${deleteKeyId}`, {
        swap: "none",
      })
      .then(() => {
        hideDeleteModal();
        document.body.dispatchEvent(new CustomEvent("apiKeyDeleted"));
      })
      .catch(() => {
        alert("Error deleting API key. Please try again.");
      });
  }
}

function handleCreateResponse(event) {
  if (event.detail.xhr.status === 200) {
    try {
      const response = JSON.parse(event.detail.xhr.responseText);

      const keyDisplay = `
                <div class="api-key-display">
                    <div style="margin-bottom: 1rem; color: #333; font-weight: 500;">Your API Key:</div>
                    <div style="background: white; padding: 1rem; border: 2px solid #4CAF50; border-radius: 4px; margin-bottom: 1rem;">
                        <code style="color: #1a1a1a; font-size: 14px; font-family: 'Courier New', monospace; word-break: break-all; user-select: all;">${
                          response.key
                        }</code>
                    </div>
                    <div style="text-align: right;">
                        <wa-button onclick="copyToClipboard('${response.key.replace(
                          /'/g,
                          "\\'"
                        )}'); this.textContent='Copied!'; setTimeout(() => this.innerHTML='<wa-icon name=\\'copy\\'></wa-icon> Copy', 2000)" size="small" variant="brand">
                            <wa-icon name="copy"></wa-icon>
                            Copy
                        </wa-button>
                    </div>
                </div>
            `;

      document.getElementById("new-api-key-display").innerHTML = keyDisplay;
      hideCreateModal();
      document.getElementById("show-key-modal").classList.remove("hidden");
    } catch (error) {
      alert("Error creating API key. Please try again.");
    }
  } else {
    alert("Error creating API key. Please try again.");
  }
}

function copyToClipboard(text) {
  navigator.clipboard
    .writeText(text)
    .then(() => {})
    .catch(() => {
      const textArea = document.createElement("textarea");
      textArea.value = text;
      document.body.appendChild(textArea);
      textArea.select();
      try {
        document.execCommand("copy");
      } catch (err) {
        alert("Failed to copy to clipboard. Please copy manually.");
      }
      document.body.removeChild(textArea);
    });
}

document.addEventListener("htmx:configRequest", function (event) {
  if (event.target.id === "create-api-key-form") {
    if (!event.detail.parameters.name || event.detail.parameters.name.trim() === "") {
      event.detail.parameters.name = "Secret key";
    }
  }
});

document.addEventListener("click", function (event) {
  const modals = document.querySelectorAll(".modal-overlay");
  modals.forEach(modal => {
    if (event.target === modal) {
      modal.classList.add("hidden");
    }
  });
});

document.addEventListener("keydown", function (event) {
  if (event.key === "Escape") {
    document.querySelectorAll(".modal-overlay").forEach(modal => {
      modal.classList.add("hidden");
    });
  }
});
