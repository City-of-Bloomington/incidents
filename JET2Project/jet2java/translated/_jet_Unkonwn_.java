package translated;

import org.eclipse.jet.JET2Context;
import org.eclipse.jet.JET2Template;
import org.eclipse.jet.JET2Writer;

public class _jet_Unkonwn_ implements JET2Template {

    public _jet_Unkonwn_() {
        super();
    }

    private static final String NL = System.getProperty("line.separator"); //$NON-NLS-1$

    public void generate(final JET2Context context, final JET2Writer __out) {
        JET2Writer out = __out;
        out.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");  //$NON-NLS-1$        
        out.write(NL);         
        out.write("<c:dump select=\"/*\" format=\"true\" entities=\"true\"/>");  //$NON-NLS-1$        
        out.write(NL);         
    }
}
