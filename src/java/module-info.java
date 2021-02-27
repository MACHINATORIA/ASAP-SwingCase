module asap.swingCase {
	requires transitive asap.swingInterface;
	requires transitive asap.smartcard;

	exports asap.ui.swing.useCase.controller;
	exports asap.ui.swing.useCase.panel;
}