let deleteKeyId = null;

function showCreateModal() {
  document.getElementById("create-modal").classList.remove("hidden");
  document.querySelector('#create-api-key-form input[name="name"]').focus();
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
  console.log("Response received:", event.detail);

  if (event.detail.xhr.status === 200) {
    try {
      const response = JSON.parse(event.detail.xhr.responseText);
      console.log("Parsed response:", response);

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
                        )}'); this.textContent='Copied!'; setTimeout(() => this.innerHTML='<span>📋</span> Copy', 2000)" size="small" variant="brand">
                            <span>📋</span>
                            Copy
                        </wa-button>
                    </div>
                </div>
            `;

      document.getElementById("new-api-key-display").innerHTML = keyDisplay;
      hideCreateModal();
      document.getElementById("show-key-modal").classList.remove("hidden");
    } catch (error) {
      console.error("Error parsing response:", error);
      alert("Error creating API key. Please try again.");
    }
  } else {
    console.error("Error response:", event.detail.xhr.status, event.detail.xhr.responseText);
    alert("Error creating API key. Please try again.");
  }
}

function copyToClipboard(text) {
  navigator.clipboard
    .writeText(text)
    .then(() => {
      console.log("API key copied to clipboard");
    })
    .catch(() => {
      const textArea = document.createElement("textarea");
      textArea.value = text;
      document.body.appendChild(textArea);
      textArea.select();
      try {
        document.execCommand("copy");
        console.log("API key copied to clipboard");
      } catch (err) {
        alert("Failed to copy to clipboard. Please copy manually.");
      }
      document.body.removeChild(textArea);
    });
}

// Event listeners for modal management
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
