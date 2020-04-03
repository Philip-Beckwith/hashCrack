public class WordGen {

    private CharSet[] word;
    StringBuilder stringBuilder = new StringBuilder();
    WordGen(int size, char[] charSet) {
        word = new CharSet[size];
        for (int i=0; i<size; i++){
           word[i] = new CharSet(charSet);
        }
    }

    boolean hasNext(){
        for (CharSet letter : word){
            if(letter.hasNext()){
                return true;
            }
        }
        return false;
    }

    public String get(){
        stringBuilder.setLength(0);
        for (CharSet letter:word){
            stringBuilder.append(letter.getChar());
        }
        return stringBuilder.toString();
    }

    public boolean setNext(){
        boolean flag = hasNext();
        for(CharSet letter : word){
            if(letter.setNext()){
                break;
            }
        }
        return flag;
    }

}
