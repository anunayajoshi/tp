package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POLICY_FREQUENCY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POLICY_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POLICY_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POLICY_PREMIUM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POLICY_START_DATE;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.client.Client;
import seedu.address.model.client.policy.CustomDate;
import seedu.address.model.client.policy.Frequency;
import seedu.address.model.client.policy.Policy;
import seedu.address.model.client.policy.PolicyName;
import seedu.address.model.client.policy.Premium;
import seedu.address.model.client.policy.UniquePolicyList;

public class EditPolicyCommand extends Command {

    public static final String COMMAND_WORD = "editPolicy";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the policy from the client identified by the index number used in the display list and"
            + " the policy identified by the index number used in the displayed policy list associated to the client.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_POLICY_INDEX + "POLICY INDEX"
            + "[" + PREFIX_POLICY_NAME + "POLICY NAME] "
            + "[" + PREFIX_POLICY_START_DATE + "START DATE] "
            + "[" + PREFIX_POLICY_PREMIUM + "PREMIUM] "
            + "[" + PREFIX_POLICY_FREQUENCY + "FREQUENCY]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_POLICY_NAME + "Travel Insurance "
            + PREFIX_POLICY_PREMIUM + "2000";
    public static final String MESSAGE_EDIT_POLICY_SUCCESS = "Edited Policy: %1$s";

    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";

    public static  final String MESSAGE_DUPLICATE_POLICY = "This policy already exists in this client.";

    private final Index clientIndex;
    private final Index policyIndex;
    private final EditPolicyDescriptor editPolicyDescriptor;

    /** Creates an EditPolicyCommand to edit the specified {@code Policy} given the {@code Client} index.
     * @param clientIndex The index of the client in the client list.
     * @param policyIndex The index of the policy display from the client.
     * @param editPolicyDescriptor The details to edit the policy with.
     */
    public EditPolicyCommand(Index clientIndex, Index policyIndex, EditPolicyDescriptor editPolicyDescriptor) {
        requireAllNonNull(clientIndex, policyIndex);
        requireNonNull(editPolicyDescriptor);

        this.clientIndex = clientIndex;
        this.policyIndex = policyIndex;
        this.editPolicyDescriptor = editPolicyDescriptor;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Client> lastShownList = model.getFilteredClientList();
        if (clientIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CLIENT_DISPLAYED_INDEX);
        }
        Client clientToEditPolicy = lastShownList.get(clientIndex.getZeroBased());

        List<Policy> lastShownPolicyList = clientToEditPolicy.getFilteredPolicyList();
        if (policyIndex.getZeroBased() >= lastShownPolicyList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_POLICY_DISPLAYED_INDEX);
        }
        Policy policyToEdit = lastShownPolicyList.get(policyIndex.getZeroBased());
        Policy editedPolicy = createEditedPolicy(policyToEdit, editPolicyDescriptor);

        UniquePolicyList clientPolicyList = clientToEditPolicy.getPolicyList();
        if (!policyToEdit.isSamePolicy(editedPolicy) && clientPolicyList.contains(editedPolicy)) {
            throw new CommandException(MESSAGE_DUPLICATE_POLICY);
        }

        clientPolicyList.setPolicy(policyToEdit, editedPolicy);
        return new CommandResult(generateSuccessMessage(clientToEditPolicy, policyToEdit));
    }

    private String generateSuccessMessage(Client client, Policy policy) {
        return String.format(
              MESSAGE_EDIT_POLICY_SUCCESS, policy.toString()) + " from: "
                    + client.getName().toString();
    }

    private static Policy createEditedPolicy(Policy policyToEdit, EditPolicyDescriptor editPolicyDescriptor) {
        assert policyToEdit != null;

        PolicyName updatedPolicyName = editPolicyDescriptor.getPolicyName().orElse(policyToEdit.getPolicyName());
        CustomDate updatedCustomDate = editPolicyDescriptor.getStartDate().orElse(policyToEdit.getStartDate());
        Premium updatedPremium = editPolicyDescriptor.getPremium().orElse(policyToEdit.getPremium());
        Frequency updatedFrequency = editPolicyDescriptor.getFrequency().orElse(policyToEdit.getFrequency());

        return new Policy(updatedPolicyName, updatedCustomDate, updatedPremium, updatedFrequency);
    }

    /**
     * Copy constructor.
     */
    public static class EditPolicyDescriptor {
        private PolicyName policyName;
        private CustomDate startDate;
        private Premium premium;
        private Frequency frequency;

        public EditPolicyDescriptor() {

        }

        public EditPolicyDescriptor(EditPolicyDescriptor toCopy) {
            setPolicyName(toCopy.policyName);
            setStartDate(toCopy.startDate);
            setPremium(toCopy.premium);
            setFrequency(toCopy.frequency);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(policyName, startDate, premium, frequency);
        }

        public void setPolicyName(PolicyName policyName) {
            this.policyName = policyName;
        }

        public Optional<PolicyName> getPolicyName() {
            return Optional.ofNullable(policyName);
        }

        public void setStartDate(CustomDate startDate) {
            this.startDate = startDate;
        }

        public Optional<CustomDate> getStartDate() {
            return Optional.ofNullable(startDate);
        }

        public void setPremium(Premium premium) {
            this.premium = premium;
        }

        public Optional<Premium> getPremium() {
            return Optional.ofNullable(premium);
        }

        public void setFrequency(Frequency frequency) {
            this.frequency = frequency;
        }

        public Optional<Frequency> getFrequency() {
            return Optional.ofNullable(frequency);
        }
        @Override
        public  boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            if (!(other instanceof EditPolicyDescriptor)) {
                return false;
            }

            EditPolicyDescriptor e = (EditPolicyDescriptor) other;

            return getPolicyName().equals(e.getPolicyName())
                    && getStartDate().equals(e.getStartDate())
                    && getPremium().equals(e.getPremium())
                    && getFrequency().equals(e.getFrequency());
        }

    }
}
