const API_BASE = "/api";
const AUTH_TOKEN = "demo-token";

let currentTicketId = null;

async function apiFetch(url, options = {}) {

    const headers = {
        ...(options.headers || {}),
        "X-TrustDesk-Token": AUTH_TOKEN
    };

    return fetch(url, {
        ...options,
        headers
    });
}
/* =========================
   SHOW SECTION
========================= */

function showSection(sectionId) {

    document.querySelectorAll(".section")
        .forEach(section => {
            section.classList.remove("active");
        });


    document.getElementById(sectionId)
        .classList.add("active");


    document.querySelectorAll(".nav-item")
        .forEach(button => {
            button.classList.remove("active");
        });


    /* Tickets */

    if (sectionId === "tickets") {

        document
            .querySelector(".nav-item:nth-child(1)")
            .classList.add("active");

        document.getElementById("pageTitle")
            .textContent = "Support Tickets";

        document.getElementById("pageSubtitle")
            .textContent =
            "Manage customer support cases with AI assistance";

        loadTickets();
    }


    /* Create */

    if (sectionId === "create") {

        document
            .querySelector(".nav-item:nth-child(2)")
            .classList.add("active");

        document.getElementById("pageTitle")
            .textContent = "Create Ticket";

        document.getElementById("pageSubtitle")
            .textContent =
            "Open a new customer support case";

        loadCustomers();
    }


    /* Ticket Detail */

    if (sectionId === "ticketDetail") {

        document
            .querySelector(".nav-item:nth-child(1)")
            .classList.add("active");

        document.getElementById("pageTitle")
            .textContent = "Ticket Details";

        document.getElementById("pageSubtitle")
            .textContent =
            "Review the ticket and run AI support workflows";
    }


    /* Evaluations */

    if (sectionId === "evaluations") {

        document
            .querySelector(".nav-item:nth-child(3)")
            .classList.add("active");

        document.getElementById("pageTitle")
            .textContent = "Evaluations";

        document.getElementById("pageSubtitle")
            .textContent =
            "Monitor AI quality and safety";
    }
}



/* =========================
   LOAD TICKETS
========================= */

async function loadTickets() {

    const container =
        document.getElementById("ticketList");


    container.innerHTML =
        '<div class="loading">Loading tickets...</div>';


    try {

        const response =
            await apiFetch(`${API_BASE}/tickets`);


        if (!response.ok) {
            throw new Error("Failed to load tickets");
        }


        const tickets =
            await response.json();


        if (tickets.length === 0) {

            container.innerHTML =
                '<div class="loading">No tickets found.</div>';

            return;
        }


        container.innerHTML =
            tickets.map(ticket => {

                const ticketId =
                    ticket.ticket_id || ticket.ticketId;

                const subject =
                    ticket.subject || "";

                const channel =
                    ticket.channel || "";

                const status =
                    ticket.status || "";


                return `

                    <div
                        class="ticket-row"
                        onclick="openTicket('${ticketId}')">

                        <div class="ticket-id">
                            ${ticketId}
                        </div>

                        <div class="ticket-subject">
                            ${escapeHtml(subject)}
                        </div>

                        <div class="ticket-channel">
                            ${channel}
                        </div>

                        <div class="status">
                            ${status}
                        </div>

                    </div>

                `;

            }).join("");


    } catch (error) {

        container.innerHTML =
            `<div class="loading">
                Failed to load tickets.
            </div>`;

        console.error(error);
    }
}



/* =========================
   LOAD CUSTOMERS
========================= */

async function loadCustomers() {

    const customerSelect =
        document.getElementById("customer");


    customerSelect.innerHTML =
        '<option value="">Loading customers...</option>';


    try {

        const response =
            await apiFetch(`${API_BASE}/customers`);


        if (!response.ok) {
            throw new Error("Failed to load customers");
        }


        const customers =
            await response.json();


        customerSelect.innerHTML =
            '<option value="">Select customer</option>';


        customers.forEach(customer => {

            const option =
                document.createElement("option");


            option.value =
                customer.customer_id || customer.customerId;


            option.textContent =
                `${customer.name} — ${customer.customer_id || customer.customerId}`;


            customerSelect.appendChild(option);

        });


    } catch (error) {

        customerSelect.innerHTML =
            '<option value="">Unable to load customers</option>';

        console.error(error);
    }
}



/* =========================
   CUSTOMER → ORDERS
========================= */

document
    .getElementById("customer")
    .addEventListener("change", async function () {


        const customerId =
            this.value;


        const orderSelect =
            document.getElementById("order");


        orderSelect.innerHTML =
            '<option value="">Loading orders...</option>';


        if (!customerId) {

            orderSelect.innerHTML =
                '<option value="">No order / Not applicable</option>';

            return;
        }


        try {

            const response =
                await apiFetch(
                    `${API_BASE}/orders/customer/${customerId}`
                );


            if (!response.ok) {
                throw new Error("Failed to load orders");
            }


            const orders =
                await response.json();


            orderSelect.innerHTML =
                '<option value="">No order / Not applicable</option>';


            orders.forEach(order => {

                const option =
                    document.createElement("option");


                const orderId =
                    order.order_id || order.orderId;


                option.value =
                    orderId;


                const itemName =
                    order.items &&
                    order.items.length > 0
                        ? order.items[0].name
                        : "Order";


                option.textContent =
                    `${orderId} — ${itemName} — ${order.currency} ${order.total}`;


                orderSelect.appendChild(option);

            });


        } catch (error) {

            orderSelect.innerHTML =
                '<option value="">Unable to load orders</option>';

            console.error(error);
        }
    });



/* =========================
   CREATE TICKET
========================= */

document
    .getElementById("ticketForm")
    .addEventListener("submit", async function (event) {


        event.preventDefault();


        const message =
            document.getElementById("createMessage");


        message.textContent =
            "Creating ticket...";


        const request = {

            customerId:
                document.getElementById("customer").value,

            orderId:
                document.getElementById("order").value || null,

            channel:
                document.getElementById("channel").value,

            subject:
                document.getElementById("subject").value,

            body:
                document.getElementById("body").value
        };


        try {

            const response =
                await apiFetch(`${API_BASE}/tickets`, {

                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify(request)
                });


            if (!response.ok) {
                throw new Error("Failed to create ticket");
            }


            const ticket =
                await response.json();


            const ticketId =
                ticket.ticket_id || ticket.ticketId;


            message.textContent =
                `Ticket ${ticketId} created successfully.`;


            document
                .getElementById("ticketForm")
                .reset();


            setTimeout(() => {

                showSection("tickets");

            }, 1000);


        } catch (error) {

            message.textContent =
                "Unable to create ticket.";

            console.error(error);
        }
    });



/* =========================
   OPEN TICKET
========================= */

async function openTicket(ticketId) {
currentAction = null;

const actionBox = document.getElementById("sensitiveActionResult");

if (actionBox) {
    actionBox.innerHTML = "";
}
    currentTicketId =
        ticketId;


    showSection("ticketDetail");


    document
        .getElementById("detailSubject")
        .textContent =
        "Loading ticket...";


    document
        .getElementById("detailTicketId")
        .textContent =
        ticketId;


    document
        .getElementById("detailCustomer")
        .textContent =
        "Loading...";


    document
        .getElementById("detailOrder")
        .textContent =
        "Loading...";


    document
        .getElementById("detailBody")
        .textContent =
        "Loading...";


    try {

        const response =
            await apiFetch(
                `${API_BASE}/tickets/${ticketId}`
            );


        if (!response.ok) {
            throw new Error("Failed to load ticket");
        }


        const data =
            await response.json();


        const ticket =
            data.ticket;

        const customer =
            data.customer;

        const order =
            data.order;


        const actualTicketId =
            ticket.ticket_id || ticket.ticketId;


        const customerId =
            customer
                ? (customer.customer_id || customer.customerId)
                : null;


        const orderId =
            order
                ? (order.order_id || order.orderId)
                : null;


        document
            .getElementById("detailSubject")
            .textContent =
            ticket.subject || "";


        document
            .getElementById("detailTicketId")
            .textContent =
            actualTicketId;


        document
            .getElementById("detailCustomer")
            .textContent =
            customer
                ? `${customer.name} — ${customerId}`
                : "Unknown";


        document
            .getElementById("detailOrder")
            .textContent =
            orderId || "No order";


        document
            .getElementById("detailChannel")
            .textContent =
            ticket.channel || "";


        document
            .getElementById("detailStatus")
            .textContent =
            ticket.status || "";


        document
            .getElementById("detailBody")
            .textContent =
            ticket.body || "";


        document
            .getElementById("triageResult")
            .innerHTML = "";
     const draftResult =
         document.getElementById("draftResult");

     if (draftResult) {
         draftResult.innerHTML = "";
     }


    } catch (error) {

        console.error(error);


        document
            .getElementById("detailSubject")
            .textContent =
            "Unable to load ticket.";


        document
            .getElementById("detailBody")
            .textContent =
            "An error occurred while loading the ticket.";
    }
}



/* =========================
   RUN TRIAGE
   PLACEHOLDER FOR NEXT STEP
========================= */

async function runTriage() {
    if (!currentTicketId) {
        alert("No ticket selected.");
        return;
    }

    const resultBox = document.getElementById("triageResult");

    resultBox.style.display = "block";
    resultBox.innerHTML = `
        <div class="ai-loading">
            🤖 AI is analyzing the ticket...
        </div>
    `;

    try {
        const response = await apiFetch(
            `${API_BASE}/agent/triage/${currentTicketId}`,
            {
                method: "POST"
            }
        );

        if (!response.ok) {
            throw new Error(`Triage failed: ${response.status}`);
        }

        const result = await response.json();

        resultBox.innerHTML = `
            <div class="ai-result">

                <h3>🤖 AI Triage Result</h3>

                <div class="detail-grid">

                    <div class="detail-item">
                        <div class="detail-label">Category</div>
                        <strong>${escapeHtml(result.category)}</strong>
                    </div>

                    <div class="detail-item">
                        <div class="detail-label">Priority</div>
                        <strong>${escapeHtml(result.priority)}</strong>
                    </div>

                    <div class="detail-item">
                        <div class="detail-label">Sentiment</div>
                        <strong>${escapeHtml(result.sentiment)}</strong>
                    </div>

                    <div class="detail-item">
                        <div class="detail-label">Escalation</div>
                        <strong>
                            ${result.escalationRequired
                                ? "⚠️ Required"
                                : "✅ Not Required"}
                        </strong>
                    </div>

                </div>

                <div class="detail-message">
                    <div class="detail-label">AI Reason</div>
                    <p>${escapeHtml(result.reason)}</p>
                </div>

                <div class="ai-actions">
                    <button
                        class="primary-button"
                        onclick="generateDraft()">
                        ✉️ Generate Draft Reply
                    </button>
                </div>

                <div id="draftResult"></div>

            </div>
        `;

    } catch (error) {

        console.error("Triage error:", error);

        resultBox.innerHTML = `
            <div class="ai-result">
                <h3>❌ Triage Failed</h3>
                <p>${escapeHtml(error.message)}</p>
            </div>
        `;
    }
}

async function generateDraft() {

    if (!currentTicketId) {
        alert("No ticket selected.");
        return;
    }

    const draftBox = document.getElementById("draftResult");

    draftBox.innerHTML = `
        <div class="ai-loading">
            ✉️ AI is generating a customer reply...
        </div>
    `;

    try {

        const response = await apiFetch(
            `${API_BASE}/agent/draft/${currentTicketId}`,
            {
                method: "POST"
            }
        );

        if (!response.ok) {
            throw new Error(`Draft generation failed: ${response.status}`);
        }

        const draft = await response.json();

       const citations = draft.citations || [];

       const citationHtml = citations.length > 0
           ? citations.map(citation => `
               <div class="citation-item">
                   <span class="citation">
                       ${escapeHtml(citation)}
                   </span>

                   <button
                       class="citation-button"
                       onclick="viewCitation('${escapeHtml(citation)}')">
                       📖 View Policy
                   </button>

                   <div
                       id="citation-${escapeHtml(citation)}"
                       class="citation-content">
                   </div>
               </div>
             `).join("")
           : "<span>No citations returned</span>";

        draftBox.innerHTML = `
            <div class="ai-result draft-result">

                <h3>✉️ Draft Reply</h3>

                <div class="detail-message customer-message">
                    <div class="detail-label">
                        Customer-facing response
                    </div>

                    <p>${escapeHtml(draft.body)}</p>
                </div>

                <div class="detail-message">

                    <div class="detail-label">
                        📚 Knowledge Citations
                    </div>

                    <div class="citations">
                        ${citationHtml}
                    </div>

                </div>

                <div class="detail-message">

                    <div class="detail-label">
                        Escalation
                    </div>

                    <p>
                        ${draft.escalationRequired
                            ? "⚠️ Human review recommended"
                            : "✅ No human escalation recommended"}
                    </p>

                    ${
                        draft.reason
                            ? `<p><strong>Reason:</strong>
                               ${escapeHtml(draft.reason)}</p>`
                            : ""
                    }

                </div>

            </div>
        `;
showSensitiveAction();
    } catch (error) {

        console.error("Draft generation error:", error);

        draftBox.innerHTML = `
            <div class="ai-result">
                <h3>❌ Draft Generation Failed</h3>
                <p>${escapeHtml(error.message)}</p>
            </div>
        `;

    }
}
async function viewCitation(docId) {

    const contentBox =
        document.getElementById(`citation-${docId}`);

    if (!contentBox) {
        return;
    }

    contentBox.innerHTML = `
        <div class="ai-loading">
            Loading policy...
        </div>
    `;

    try {

        const response = await apiFetch(
            `${API_BASE}/knowledge/${encodeURIComponent(docId)}`
        );

        if (!response.ok) {
            throw new Error(
                `Failed to load citation: ${response.status}`
            );
        }

        const document = await response.json();

        contentBox.innerHTML = `
            <div class="citation-policy">

                <h4>
                    ${escapeHtml(document.title)}
                </h4>

                <div class="citation-metadata">
                    <span>
                        Document ID:
                        ${escapeHtml(document.docId)}
                    </span>

                    <span>
                        Source:
                        ${escapeHtml(document.sourcePath || "N/A")}
                    </span>
                </div>

                <div class="citation-text">
                    ${escapeHtml(document.content)}
                </div>

            </div>
        `;

    } catch (error) {

        console.error("Citation error:", error);

        contentBox.innerHTML = `
            <div class="citation-error">
                ❌ Unable to load this knowledge document.
            </div>
        `;
    }
}
let currentAction = null;


/* =========================
   SENSITIVE ACTION
========================= */

async function showSensitiveAction() {

    if (!currentTicketId) {
        return;
    }

    const actionBox =
        document.getElementById("sensitiveActionResult");

    actionBox.innerHTML = `
        <div class="sensitive-action">
            <div class="ai-loading">
                🔐 Preparing sensitive action review...
            </div>
        </div>
    `;

    try {

        const response = await apiFetch(
            `${API_BASE}/agent/action/${currentTicketId}`,
            {
                method: "POST"
            }
        );

        if (!response.ok) {

            throw new Error(
                `Action creation failed: ${response.status}`
            );
        }

        const action = await response.json();

        // Store the complete action so Approve/Reject
        // can use its idempotency key.
        currentAction = action;

        renderSensitiveAction(action);

    } catch (error) {

        console.error(
            "Sensitive action error:",
            error
        );

        actionBox.innerHTML = `
            <div class="ai-result">

                <h3>❌ Sensitive Action Failed</h3>

                <p>
                    ${escapeHtml(error.message)}
                </p>

            </div>
        `;
    }
}


/* =========================
   RENDER ACTION
========================= */

function renderSensitiveAction(action) {

    const actionBox =
        document.getElementById("sensitiveActionResult");

    const actionName =
        action.action === "create_replacement_order"
            ? "Create Replacement Order"
            : action.action;

    // Build AI evidence section
    const evidenceHtml =
        action.evidence && action.evidence.length > 0
            ? action.evidence.map(docId => `
                <div class="evidence-item">

                    <span class="evidence-id">
                        ${escapeHtml(docId)}
                    </span>

                    <button
                        type="button"
                        onclick="viewCitation('${escapeHtml(docId)}')">

                        View Policy

                    </button>

                </div>
            `).join("")
            : `
                <div class="no-evidence">
                    No supporting policy evidence was returned.
                </div>
            `;

    actionBox.innerHTML = `
        <div class="sensitive-action">

            <h3>
                🔐 Sensitive Action Review
            </h3>

            <div class="action-warning">
                ⚠️ This action requires human approval
                before execution.
            </div>

            <div class="action-details">

                <div class="action-item">

                    <div class="action-label">
                        Action
                    </div>

                    <strong>
                        ${escapeHtml(actionName)}
                    </strong>

                </div>

                <div class="action-item">

                    <div class="action-label">
                        Ticket
                    </div>

                    <strong>
                        ${escapeHtml(action.ticketId)}
                    </strong>

                </div>

                <div class="action-item">

                    <div class="action-label">
                        Customer
                    </div>

                    <strong>
                        ${escapeHtml(action.customerId)}
                    </strong>

                </div>

                <div class="action-item">

                    <div class="action-label">
                        Order
                    </div>

                    <strong>
                        ${escapeHtml(action.orderId || "N/A")}
                    </strong>

                </div>

                <div class="action-item">

                    <div class="action-label">
                        Reason
                    </div>

                    <strong>
                        ${escapeHtml(action.reason)}
                    </strong>

                </div>

                <!-- AI Evidence -->

                <div class="action-item action-evidence">

                    <div class="action-label">
                        AI Evidence
                    </div>

                    <div class="evidence-list">
                        ${evidenceHtml}
                    </div>

                </div>

                <div class="action-item">

                    <div class="action-label">
                        Risk Level
                    </div>

                    <strong>
                        ${escapeHtml(action.riskLevel)}
                    </strong>

                </div>

                <div class="action-item">

                    <div class="action-label">
                        Approval Status
                    </div>

                    <strong id="actionApprovalStatus">
                        ${escapeHtml(action.approvalStatus)}
                    </strong>

                </div>

                <div class="action-item">

                    <div class="action-label">
                        Execution Status
                    </div>

                    <strong id="actionExecutionStatus">
                        ${escapeHtml(action.executionStatus)}
                    </strong>

                </div>

            </div>

            <div class="action-buttons">

                <button
                    id="approveActionButton"
                    class="approve-button"
                    onclick="approveSensitiveAction()">

                    ✓ Approve

                </button>

                <button
                    id="rejectActionButton"
                    class="reject-button"
                    onclick="rejectSensitiveAction()">

                    ✕ Reject

                </button>

            </div>

        </div>
    `;
}
/* =========================
   APPROVE ACTION
========================= */

async function approveSensitiveAction() {

    if (!currentAction) {

        alert("No sensitive action is available.");

        return;
    }

    const idempotencyKey =
        currentAction.idempotencyKey;

    try {

        // STEP 1: Human approval
        const approvalResponse = await apiFetch(
            `${API_BASE}/actions/${encodeURIComponent(idempotencyKey)}/approve`,
            {
                method: "POST"
            }
        );

        if (!approvalResponse.ok) {

            const message =
                await approvalResponse.text();

            throw new Error(
                message || `Approval failed: ${approvalResponse.status}`
            );
        }

        const approvedAction =
            await approvalResponse.json();

        currentAction = approvedAction;

        renderSensitiveAction(approvedAction);


        // STEP 2: Execute the approved action
        const executionResponse = await apiFetch(
            `${API_BASE}/agent/execute/${encodeURIComponent(idempotencyKey)}`,
            {
                method: "POST"
            }
        );

        if (!executionResponse.ok) {

            const message =
                await executionResponse.text();

            throw new Error(
                message || `Execution failed: ${executionResponse.status}`
            );
        }

        const executionResult =
            await executionResponse.json();

        console.log(
            "Agent execution result:",
            executionResult
        );


        // STEP 3: Update the UI
        const executionStatus =
            document.getElementById("actionExecutionStatus");

        if (executionStatus) {
            executionStatus.textContent =
                executionResult.status;
        }

        const approvalStatus =
            document.getElementById("actionApprovalStatus");

        if (approvalStatus) {
            approvalStatus.textContent =
                "APPROVED";
        }

        alert(
            executionResult.agentResponse ||
            executionResult.message ||
            "Action executed successfully."
        );

    } catch (error) {

        console.error(
            "Approval/execution error:",
            error
        );

        alert(
            `Action failed: ${error.message}`
        );
    }
}
/* =========================
   REJECT ACTION
========================= */

async function rejectSensitiveAction() {

    if (!currentAction) {

        alert("No sensitive action is available.");

        return;
    }

    const idempotencyKey =
        currentAction.idempotencyKey;

    try {

        const response = await apiFetch(
            `${API_BASE}/actions/${encodeURIComponent(idempotencyKey)}/reject`,
            {
                method: "POST"
            }
        );

        if (!response.ok) {

            const message =
                await response.text();

            throw new Error(
                message || `Rejection failed: ${response.status}`
            );
        }

        const updatedAction =
            await response.json();

        currentAction = updatedAction;

        renderSensitiveAction(updatedAction);

    } catch (error) {

        console.error(
            "Rejection error:",
            error
        );

        alert(
            `Rejection failed: ${error.message}`
        );
    }
}

/* =========================
   HTML SAFETY
========================= */

function escapeHtml(value) {

    if (!value) {
        return "";
    }


    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}


async function runEvaluation() {

    const button = document.getElementById("runEvaluationButton");
    const status = document.getElementById("evaluationStatus");
    const summary = document.getElementById("evaluationSummary");
    const results = document.getElementById("evaluationResults");

    button.disabled = true;
    button.innerText = "⏳ Running...";

    status.innerHTML = "Starting evaluation...";
    summary.innerHTML = "";
    results.innerHTML = "";

    try {

        // Start background evaluation
        const startResponse = await apiFetch(
            `${API_BASE}/evaluation/run`,
            {
                method: "POST"
            }
        );

        if (!startResponse.ok) {
            throw new Error(
                `Failed to start evaluation: ${startResponse.status}`
            );
        }

        // Poll for completion
        await pollEvaluationStatus();

    } catch (error) {

        console.error("Evaluation error:", error);

        status.innerHTML =
            `<div class="error-message">
                Evaluation failed: ${error.message}
             </div>`;

        button.disabled = false;
        button.innerText = "▶ Run Evaluation";
    }
}
async function pollEvaluationStatus() {

    const button = document.getElementById("runEvaluationButton");
    const status = document.getElementById("evaluationStatus");

    const response = await apiFetch(
        `${API_BASE}/evaluation/status`
    );

    if (!response.ok) {
        throw new Error(
            `Status check failed: ${response.status}`
        );
    }

    const data = await response.json();

    if (data.status === "RUNNING") {

        status.innerHTML =
            `<div class="message">
                ⏳ Evaluation is running in the background...
             </div>`;

        setTimeout(
            pollEvaluationStatus,
            2000
        );

        return;
    }

    if (data.status === "COMPLETED" || data.totalCases !== undefined) {

        status.innerHTML =
            `<div class="success-message">
                Evaluation completed successfully.
             </div>`;

        displayEvaluationSummary(data);

        button.disabled = false;
        button.innerText = "▶ Run Evaluation";

        return;
    }

    if (data.status === "IDLE") {

        status.innerHTML =
            `<div class="message">
                Waiting for evaluation...
             </div>`;

        setTimeout(
            pollEvaluationStatus,
            1000
        );

        return;
    }

    throw new Error(
        `Unknown evaluation status: ${data.status}`
    );
}
function displayEvaluationSummary(data) {

    const summary =
        document.getElementById("evaluationSummary");

    summary.innerHTML = `
        <div class="evaluation-card">

            <h3>Evaluation Summary</h3>

            <div class="evaluation-metrics">

                <div>
                    <strong>${data.totalCases}</strong>
                    <span>Total Cases</span>
                </div>

                <div>
                    <strong>
                        ${formatPercentage(data.triageAccuracy)}
                    </strong>
                    <span>Triage Accuracy</span>
                </div>

                <div>
                    <strong>
                        ${formatPercentage(data.escalationAccuracy)}
                    </strong>
                    <span>Escalation Accuracy</span>
                </div>

                <div>
                    <strong>
                        ${formatPercentage(data.citationCoverage)}
                    </strong>
                    <span>Citation Coverage</span>
                </div>

                <div>
                    <strong>
                        ${formatPercentage(data.unsafeActionBlockRate)}
                    </strong>
                    <span>Unsafe Action Block Rate</span>
                </div>

            </div>

            <div class="evaluation-counts">

                <p>
                    Category Correct:
                    <strong>
                        ${data.categoryCorrect}/${data.totalCases}
                    </strong>
                </p>

                <p>
                    Priority Correct:
                    <strong>
                        ${data.priorityCorrect}/${data.totalCases}
                    </strong>
                </p>

                <p>
                    Escalation Correct:
                    <strong>
                        ${data.escalationCorrect}/${data.totalCases}
                    </strong>
                </p>

            </div>

        </div>
    `;

    renderEvaluationResults(data.results);
}
function formatPercentage(value) {

    if (value === null || value === undefined) {
        return "0%";
    }

    return `${(value * 100).toFixed(1)}%`;
}
function renderEvaluationResults(results) {

    const container =
        document.getElementById("evaluationResults");

    if (!results || results.length === 0) {

        container.innerHTML =
            `<p>No evaluation results available.</p>`;

        return;
    }

    container.innerHTML = `
        <div class="evaluation-card">

            <h3>Case Results</h3>

            <div class="evaluation-table-wrapper">

                <table class="evaluation-table">

                    <thead>
                        <tr>
                            <th>Case</th>
                            <th>Ticket</th>
                            <th>Category</th>
                            <th>Priority</th>
                            <th>Escalation</th>
                            <th>Citations</th>
                        </tr>
                    </thead>

                    <tbody>

                        ${results.map(result => `

                            <tr>

                                <td>
                                    ${result.caseId}
                                </td>

                                <td>
                                    ${result.ticketId}
                                </td>

                                <td>
                                    ${result.categoryCorrect
                                        ? "✅"
                                        : "❌"}
                                    ${result.actualCategory}
                                </td>

                                <td>
                                    ${result.priorityCorrect
                                        ? "✅"
                                        : "❌"}
                                    ${result.actualPriority}
                                </td>

                                <td>
                                    ${result.escalationCorrect
                                        ? "✅"
                                        : "❌"}
                                    ${result.actualEscalation}
                                </td>

                                <td>
                                    ${formatPercentage(
                                        result.citationCoverage
                                    )}
                                </td>

                            </tr>

                        `).join("")}

                    </tbody>

                </table>

            </div>

        </div>
    `;
}
/* =========================
   INITIAL PAGE
========================= */

loadTickets();