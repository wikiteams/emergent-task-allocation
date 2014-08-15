package strategies;

/***
 * 
 * Strategy for Agent {strategy for choosing tasks}
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 1.2
 *
 */
public class Strategy {
	
	public TaskChoice taskChoice;
	public TaskMinMaxChoice taskMinMaxChoice;
	public SkillChoice skillChoice;
	
	public Strategy(TaskChoice taskChoice, TaskMinMaxChoice taskMinMaxChoice, SkillChoice skillChoice){
		this.taskChoice = taskChoice;
		this.taskMinMaxChoice = taskMinMaxChoice;
		this.skillChoice = skillChoice;
	}
	
	public enum TaskChoice {
		/**
		 * Homofilia - milosc do tego samego - szukamy takiego taska maksymalnie 
		 * podobnego nad ktorym ju¿ pracowalismy. Jezeli jeszcze nie pracowalismy
		 * nad zadnym to szukamy pierwszego mozliwie odpowiadajacego naszej
		 * macierzy umiejetnosci. Wtedy budowanie doswiadczenia bedzie w miare
		 * odpowiadalo elementom w naszej macierzy. I agent caly czas stara sie
		 * szukac podobnych taskow.
		 */
		HOMOPHYLY_EXP_BASED,
		/**
		 * Tutaj przeciwienstwo tego co wyzej, agent stara sie znalezc zupelnie
		 * inne taski niz te nad ktorym dotychczas pracowal. Jest to strategia
		 * tworzaca doswiadczenie. Jezeli agent nie pracowal jeszcze nad zadnym taskiem,
		 * to szuka takiego taska ktory zupelnie nie odpowiada macierzy jego
		 * umiejetnosci.
		 */
		HETEROPHYLY_EXP_BASED,
		/**
		 * Social vector - znajdowanie mozliwie najblizszego wektora umiejetnosci.
		 * Umiejetnosci niesie ze soba dodatkowe informacje - kategoria umiejetnosci
		 * - np. programowanie niskopoziomowe, frontend, itp. Wiec mimo ze nie posiadamy
		 * takich umiejetnosci jakie maja taski w puli, to szukamy najblizej pasujacego
		 * wektora i bierzemy ten task
		 */
		SOCIAL_VECTOR,
		/**
		 * Losowanie taska z puli dostepnych - tylko pod warunkiem ze ma choc jeden skill
		 * nad ktorym jest mozliwe pracowanie. Generalnie stosujemy Random, ale mozna 
		 * rowniez uruchomic jakis rozklad normalny.
		 */
		RANDOM,
		/**
		 * Ta strategia polega na porownywaniu siebie do innych uzytownikow, agent A
		 * stara sie nasladowac innego mozliwie podobnego do siebie uzytkownika (agent B)
		 * sprawdza gdzie pracuje agent B, i pracuje nad tym samym. Jezeli podobny do niego
		 * uzytkownik jeszcze nad niczym nie pracowal, to sprawdza co on potrafi
		 * najbardziej (to tez nasz skill - agenta A) i pracuje nad takim taskiem
		 * co który owego skilla wymaga
		 */
		COMPARISION,
		/**
		 * TO DO: uzupelnic opis
		 */
		MACHINE_LEARNED,
		/**
		 * Algorytm segreguje pierw skilly wewnatz kazdego taska (max lub min) pod wzglede zaawansowania,
		 * nastepnie algorytm segreguje taski wg wartosci max inside skill (lub min inside skill)
		 */
		ARG_MIN_MAX,
		/**
		 * Celem jest wybór najbardziej zaawansowanego taska
		 * liczymy to w ten sposob - avg(sigma(W/G)) czyli 
		 * srednia arytmetyczna zaawansowania wszystkich skilli
		 */
		PREFERENTIAL,
		/**
		 * 
		 */
		HOMOPHYLY_CLASSIC,
		/**
		 * In this strategy we take first task top from the sort result of intersection. 
		 * For agent $A_{i}$, let's consider his skills $\{S_{1}^{A_{i}},...,S_{i}^{A_{i}}\}$. 
		 * Algorithm iterates through all available tasks and searches for intersection. 
		 * It means, that if there is such task $T_{i}$ having skills $\{S_{1}^{T_{i}},...,S_{i}^{T_{i}}\}$ 
		 * which satisfies $\exists\{S_{j}^{A}\}\cap\{S_{j}^{T}\}$ and $\{S_{j}^{A}\}\cap\{S_{j}^{T}\}$ 
		 * is \textsl{not null}, than take this task into consideration and calculate sum of progress. 
		 * If there are no tasks which have a common skill with agent's skills, than agent chooses a random task.
		 */
		HETEROPHYLY_CLASSIC,
		/**
		 * In this strategy we take first task bottom from the sort result of intersection.
		 */
		CENTRAL_ASSIGNMENT
	}
	
	public enum TaskMinMaxChoice {
		ARGMAX_ARGMAX,
		ARGMIN_ARGMAX,
		ARGMAX_ARGMIN,
		ARGMIN_ARGMIN
	}
	
	public enum SkillChoice {
		/**
		 * Dla kazdego Sn pracuj rowno po czesci 1/n
		 * jezeli parametr allowRookie wlaczony, to omijaj intersekcje
		 * i pracuj nad wszystkim w danym tasku
		 */
	    PROPORTIONAL_TIME_DIVISION,
	    /**
	     * Pracuj dla wybranego Sn. Je¿eli postepy puste w kazdym ze skilli w tasku
	     * to wybierz losowy. W przeciwnym razie pracuj tylko nad tym taskiem, ktory
	     * jest najbardziej zaczety (najmniej mu do zamkniecia)
	     */
	    GREEDY_ASSIGNMENT_BY_TASK,
	    /**
	     * Pracuj wylacznie nad tym skillem, w ktory agent ma najwiecej doswiadczenia
	     */
	    CHOICE_OF_AGENT,
	    /**
	     * Pracuj zawsze nad losowo wybranym skillem
	     */
	    RANDOM
	}
	
	@Override
	public String toString(){
		return this.taskChoice.name() + "," + this.skillChoice.name();
	}

}
