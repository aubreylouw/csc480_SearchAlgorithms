package alouw.csc480.search.interfaces;

/* 
 * Enumeration of the legal operations to transform one state into another
 */
public enum ProblemTransformationOperation {
	UP {
		@Override
		public ProblemTransformationOperation reverseTransformation() {
			return DOWN;
		}
	}, DOWN {
		@Override
		public ProblemTransformationOperation reverseTransformation() {
			return UP;
		}
	}, LEFT {
		@Override
		public ProblemTransformationOperation reverseTransformation() {
			return RIGHT;
		}
	}, RIGHT {
		@Override
		public ProblemTransformationOperation reverseTransformation() {
			return LEFT;
		}
	}, NONE {
		@Override
		public ProblemTransformationOperation reverseTransformation() {
			return NONE;
		}
	};
	
	public abstract ProblemTransformationOperation reverseTransformation();
}