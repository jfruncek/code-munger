import java.io.File;
import java.nio.ByteBuffer
import java.nio.file.Files;
import java.util.List;


/**
 * This script parses Mp4 files at the binary level and writes the atoms in readable form to text files
 * 
 * @author jfruncek
 *
 */

public class ParseMp4File {

	File output
	long size = 0
	
	public static void main(String[] args) {
		List<File> files = getMp4s(args[0])
		files.each {
			println 'Parsing ' + it
			new ParseMp4File().parse(it)
		}
		
	}
	
	static List<File> getMp4s(String dirName) {
		File dir = new File(dirName)
		File[] dirs = dir.listFiles(new FileFilter() { 
							public boolean accept(File file) {
								return file.isDirectory()
							}})
		File[] files = dir.listFiles(new FileFilter() {
									public boolean accept(File file) {
										return file.name.endsWith('.mp4')
									}} ) 
		dirs.each {
			files = files + getMp4s(it.path)
		}
		return files
	}

	def parse(File file) {		
		byte[] data = file.getBytes()
		List<Atom> atoms = getAtoms(data, 0)
		output = new File(file.name + '.txt')
		output.write '{ mp4: {\n'
		write(atoms)
		output.append('size:' + size + '\n} }')
	}
	
	def write(List<Atom> atoms) {
		boolean first = true
		int level
		atoms.each {
			size += it.size
			if (first) {
				level = it.level
				writeTabs level
				output.append 'atom: [\n'
				writeTabs level + 1
				output.append 'level: ' + it.level + '\n'
				first = false
			}
			writeTabs level + 1
			output.append 'type: ' + it.cccc + ',\n'
			writeTabs level + 1
			output.append 'size: ' + it.size +',\n'
			
			if (it.atoms == null || it.atoms.size() == 0) {
				if (it.cccc == 'mdhd' || it.cccc == 'mvhd' ) {
					ByteBuffer wrapped = ByteBuffer.wrap(it.data)
					//println 'bytes: ' + wrapped
					byte version = wrapped.get(0)
					writeTabs level + 1
					output.append('version: ' + version + '\n')
					writeTabs level + 1
					output.append('flags: ' + wrapped.get(1) + ' '  + wrapped.get(2) + ' '  + wrapped.get(3) + '\n')
					if (version == 1) {
						writeTabs level + 1
						output.append('creation time: ' + wrapped.getLong(4) + '\n')
						writeTabs level + 1
						output.append('modified time: ' + wrapped.getLong(12) + '\n')
						writeTabs level + 1
						output.append('time scale: ' + wrapped.getInt(20) + '\n')
						writeTabs level + 1
						output.append('duration: ' + wrapped.getLong(24) + '\n')
					} else {
						writeTabs level + 1
						output.append('creation time: ' + wrapped.getInt(4) + '\n')
						writeTabs level + 1
						output.append('modified time: ' + wrapped.getInt(8) + '\n')
						writeTabs level + 1
						output.append('time scale: ' + wrapped.getInt(12) + '\n')
					}
					if (it.cccc == 'mvhd') {
						writeTabs level + 1
						output.append('duration: ' + wrapped.getInt(16) + '\n')
						writeTabs level + 1
						output.append('preferred rate: ' + wrapped.getInt(20) + '\n')
						writeTabs level + 1
						output.append('preferred volume: ' + wrapped.getShort(24) + '\n')			
						writeTabs level + 1
						output.append('preview time: ' + wrapped.getInt(72) + '\n')			
						writeTabs level + 1
						output.append('preview duration: ' + wrapped.getInt(76) + '\n')			
						writeTabs level + 1
						output.append('poster time: ' + wrapped.getInt(80) + '\n')			
						writeTabs level + 1
						output.append('selection time: ' + wrapped.getInt(84) + '\n')			
						writeTabs level + 1
						output.append('selection duration: ' + wrapped.getInt(88) + '\n')			
						//writeTabs level + 1
						//output.append('current time: ' + wrapped.getInt(92) + '\n')			
						//writeTabs level + 1
						//output.append('next track ID: ' + wrapped.getInt(96) + '\n')			
					}
				}
				if (it.cccc == 'tkhd') {
					ByteBuffer wrapped = ByteBuffer.wrap(it.data)
					byte version = wrapped.get(0)
					writeTabs level + 1
					output.append('version: ' + version + '\n')
					writeTabs level + 1
					output.append('flags: ' + wrapped.get(1) + ' '  + wrapped.get(2) + ' '  + wrapped.get(3) + '\n')
					if (version == 0) {
						writeTabs level + 1
						output.append('creation time: ' + wrapped.getInt(4) + '\n')
						writeTabs level + 1
						output.append('modified time: ' + wrapped.getInt(8) + '\n')
						writeTabs level + 1
						output.append('track id: ' + wrapped.getInt(12) + '\n')
						writeTabs level + 1
						output.append('duration: ' + wrapped.getInt(20) + '\n')
						writeTabs level + 1
						output.append('layer: ' + wrapped.getShort(28) + '\n')
						writeTabs level + 1
						output.append('alt group: ' + wrapped.getShort(30) + '\n')
						writeTabs level + 1
						output.append('volume: ' + wrapped.getShort(32) + '\n')
						writeTabs level + 1
						output.append('track width: ' + wrapped.getShort(70) + '\n')
						writeTabs level + 1
						output.append('track height: ' + wrapped.getShort(74) + '\n')
					}					
				}
				
				/*if (it.cccc == 'elst') {
					ByteBuffer wrapped = ByteBuffer.wrap(it.data)
					byte version = wrapped.get(0)
					writeTabs level + 1
					output.append('version: ' + version + '\n')
					writeTabs level + 1
					output.append('flags: ' + wrapped.get(1) + ' '  + wrapped.get(2) + ' '  + wrapped.get(3) + '\n')
					writeTabs level + 1
					output.append('# of entries: ' + wrapped.getInt(4) + '\n')
					writeTabs level + 1
					output.append('edit duration: ' + wrapped.getInt(8) + '\n')
					writeTabs level + 1
					output.append('edit media time: ' + wrapped.getInt(12) + '\n')
					writeTabs level + 1
					output.append('playback speed: ' + wrapped.getInt(16) + '\n')
				}*/
								
				output.append '\n'
			} else {
				write(it.atoms)
			}
		}
		writeTabs level
		output.append ']\n'
	}
	
	def writeTabs(int num) {
		for (int i = 0; i < num; i++) {
			output.append '  '
		}
	}
	
	def List<Atom> getAtoms(byte[] fileData, int level) {
		
		long start = 0
		level++
		List<Atom> atoms = new ArrayList<Atom>()		
		byte[] data 
		
		if (level == 1) println 'file size ' + fileData.size()
		
		while (start + 8 < fileData.size()) {
			data = Arrays.copyOfRange(fileData, (int) start, (int) start + 8)
			ByteBuffer wrapped = ByteBuffer.wrap(data)
			short offset = 8
			String type = ''
			boolean binary = false
			(4..7).each {
				char ch = data[it]
				if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9')) 
					type += ch
				else
					binary = true
			}
			if (binary) break
			long size = wrapped.getInt(0)
			if (size == 1) {
				offset = 16
				data = Arrays.copyOfRange(fileData, (int) start+8, (int) start+16)
				wrapped = ByteBuffer.wrap(data)
				size = wrapped.getLong()
			}
			//println 'L' + level + ' start ' + start + ' offset ' + offset + ' size ' + size + ' bytes ' + fileData.size()
			Atom atom = new Atom(size, type, level)
			if (size - offset > 0 && size < fileData.size()) {
				int readSize = (int) size - offset
				//println 'readSize ' + readSize
				if (readSize > 0 && level <= 8) {
					data = Arrays.copyOfRange(fileData, (int) start+offset, (int) start+readSize)
					atom.data = data
					atom.atoms = getAtoms(data, level)
				}
			}
			atoms.add(atom)
			if (size <= 0) break			
			start += size
		} 
		return atoms
	}
	
	class Atom {
		long size
		String cccc
		List<Atom> atoms
		int level
		byte[] data
		Atom(long size, String cccc, int level) {
			this.size = size
			this.cccc = cccc
			this.level = level
		}
	}
}
