package javaSweeper;

import javax.swing.*;
import java.awt.*;
import java.lang.Math;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class menu extends JFrame implements ActionListener, ChangeListener{
	
	int x, y;
	double d;
	boolean guess = true;
	JPanel panel = new JPanel();
	JButton randomize, start, close;
	JSlider xRange, yRange, dRange;
	JToggleButton dToggle, gToggle;
	JLabel xText, yText, dText;
	
	public menu () {
		this.x = 20;
		this.y = 20;
		this.d = 15;
	}
	
	public void setup() {
		this.setTitle("Minesweeper Setup");
		xRange = new JSlider(5, 100, 20);
		yRange = new JSlider(5, 100, 20);
		dRange = new JSlider(1, 99, 20);
		
		xRange.addChangeListener(this);
		yRange.addChangeListener(this);
		dRange.addChangeListener(this);
		
		xText = new JLabel("Width: 20");
		yText = new JLabel("Height: 20");
		dText = new JLabel("Density: 20");
		
		dToggle = new JToggleButton("Percentage Bombs");
		gToggle = new JToggleButton("Guess Mode");
		randomize = new JButton("Randomize");
		start = new JButton("Start");
		close = new JButton("Close");
		
		dToggle.addActionListener(this);
		gToggle.addActionListener(this);
		randomize.addActionListener(this);
		start.addActionListener(this);
		close.addActionListener(this);
		
		panel.setLayout(new GridLayout(4, 3));
		panel.setBorder(BorderFactory.createLineBorder(Color.white, 15));
		
		this.setSize(600, 250);
		
		
		panel.add(dText);
		panel.add(dRange);
		panel.add(dToggle);
		
		panel.add(xText);
		panel.add(xRange);
		panel.add(randomize);
		
		panel.add(yText);
		panel.add(yRange);
		panel.add(start);
		
		panel.add(gToggle);
		
		panel.add(close);
		
		this.add(panel);
		
		this.setVisible(true);
	}
	
	public void start() {
		
		if (!dToggle.isSelected()) {
			this.d /= 100;
		}
		
		minesweeper game = new minesweeper(x, y, d);
		
		if (guess) {
			game.make();
		} else {
			game.noGuessStart();
		}
		
		this.dispose();
	} 
	
	@ Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == xRange) {
			this.x = xRange.getValue();
			xText.setText("Width: " + String.valueOf(this.x));
		} else if (e.getSource() == yRange) {
			this.y = yRange.getValue();
			yText.setText("Height: " + String.valueOf(this.y));
		} else if (e.getSource() == dRange) {
			this.d = dRange.getValue();
			dText.setText("Density: " + String.valueOf(this.d));
		}
		if (dToggle.isSelected()) {
			dRange.setMaximum(this.x * this.y - 1);
			dRange.setMinimum(1);
			dToggle.setText("Number of Bombs");
		}
	}
	
	@ Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == dToggle) {
			if (!dToggle.isSelected()) {
				dRange.setMaximum(99);
				dRange.setMinimum(1);
				dToggle.setText("Percentage of Bombs");
			} else {
				dRange.setMaximum(this.x * this.y - 1);
				dRange.setMinimum(1);
				dToggle.setText("Number of Bombs");
			}
		} else if (e.getSource() == gToggle){
			if (gToggle.isSelected()) {
				guess = false;
				gToggle.setText("No Guess Mode");
			} else {
				guess = true;
				gToggle.setText("Guess Mode");
			}
		} else if (e.getSource() == randomize) {
			xRange.setValue((int) (xRange.getMinimum() + Math.random()*(xRange.getMaximum() - xRange.getMinimum() + 1)));
			yRange.setValue((int) (yRange.getMinimum() + Math.random()*(yRange.getMaximum() - yRange.getMinimum() + 1)));
			dRange.setValue((int) (dRange.getMinimum() + Math.random()*(dRange.getMaximum() - dRange.getMinimum() + 1)));			
		} else if (e.getSource() == start) {
			this.start();
		} else if (e.getSource() == close) {
			this.dispose();
		}
	}
	
	private static final long serialVersionUID = 7037620478502420291L;
	
}