package org.apache.poi.ddf;

import androidx.core.app.FrameMetricsAggregator;
import androidx.core.view.InputDeviceCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.zip.InflaterInputStream;
import org.apache.poi.hssf.usermodel.HSSFShapeTypes;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.HexRead;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Units;

/* JADX INFO: loaded from: classes.dex */
public final class EscherDump {
    public void dump(byte[] data, int offset, int size, PrintStream out) {
        EscherRecordFactory recordFactory = new DefaultEscherRecordFactory();
        int pos = offset;
        while (pos < offset + size) {
            EscherRecord r = recordFactory.createRecord(data, pos);
            int bytesRead = r.fillFields(data, pos, recordFactory);
            out.println(r);
            pos += bytesRead;
        }
    }

    public void dumpOld(long maxLength, InputStream in, PrintStream out) throws IOException {
        String recordName;
        boolean atEOF;
        String recordName2;
        long remainingBytes = maxLength;
        boolean isContainer = false;
        while (!isContainer && remainingBytes > 0) {
            short options = LittleEndian.readShort(in);
            short recordId = LittleEndian.readShort(in);
            int recordBytesRemaining = LittleEndian.readInt(in);
            remainingBytes -= 8;
            if (recordId != -3806) {
                switch (recordId) {
                    case -4096:
                        recordName = "MsofbtDggContainer";
                        break;
                    case -4095:
                        recordName = "MsofbtBstoreContainer";
                        break;
                    case -4094:
                        recordName = "MsofbtDgContainer";
                        break;
                    case -4093:
                        recordName = "MsofbtSpgrContainer";
                        break;
                    case -4092:
                        recordName = "MsofbtSpContainer";
                        break;
                    case -4091:
                        recordName = "MsofbtSolverContainer";
                        break;
                    case -4090:
                        recordName = EscherDggRecord.RECORD_DESCRIPTION;
                        break;
                    case -4089:
                        recordName = EscherBSERecord.RECORD_DESCRIPTION;
                        break;
                    case -4088:
                        recordName = EscherDgRecord.RECORD_DESCRIPTION;
                        break;
                    case -4087:
                        recordName = EscherSpgrRecord.RECORD_DESCRIPTION;
                        break;
                    case -4086:
                        recordName = EscherSpRecord.RECORD_DESCRIPTION;
                        break;
                    case -4085:
                        recordName = "MsofbtOPT";
                        break;
                    case -4084:
                        recordName = "MsofbtTextbox";
                        break;
                    case -4083:
                        recordName = "MsofbtClientTextbox";
                        break;
                    case -4082:
                        recordName = "MsofbtAnchor";
                        break;
                    case -4081:
                        recordName = EscherChildAnchorRecord.RECORD_DESCRIPTION;
                        break;
                    case -4080:
                        recordName = EscherClientAnchorRecord.RECORD_DESCRIPTION;
                        break;
                    case -4079:
                        recordName = EscherClientDataRecord.RECORD_DESCRIPTION;
                        break;
                    case -4078:
                        recordName = "MsofbtConnectorRule";
                        break;
                    case -4077:
                        recordName = "MsofbtAlignRule";
                        break;
                    case -4076:
                        recordName = "MsofbtArcRule";
                        break;
                    case -4075:
                        recordName = "MsofbtClientRule";
                        break;
                    case -4074:
                        recordName = "MsofbtCLSID";
                        break;
                    case -4073:
                        recordName = "MsofbtCalloutRule";
                        break;
                    default:
                        switch (recordId) {
                            case -3816:
                                recordName = "MsofbtRegroupItem";
                                break;
                            case -3815:
                                recordName = "MsofbtSelection";
                                break;
                            case -3814:
                                recordName = "MsofbtColorMRU";
                                break;
                            default:
                                switch (recordId) {
                                    case -3811:
                                        recordName = "MsofbtDeletedPspl";
                                        break;
                                    case -3810:
                                        recordName = EscherSplitMenuColorsRecord.RECORD_DESCRIPTION;
                                        break;
                                    case -3809:
                                        recordName = "MsofbtOleObject";
                                        break;
                                    case -3808:
                                        recordName = "MsofbtColorScheme";
                                        break;
                                    default:
                                        if (recordId >= -4072 && recordId <= -3817) {
                                            recordName = "MsofbtBLIP";
                                        } else if ((options & 15) == 15) {
                                            recordName = "UNKNOWN container";
                                        } else {
                                            recordName = "UNKNOWN ID";
                                        }
                                        break;
                                }
                                break;
                        }
                        break;
                }
            } else {
                recordName = "MsofbtUDefProp";
            }
            StringBuilder stringBuf = new StringBuilder();
            stringBuf.append("  ");
            stringBuf.append(HexDump.toHex(recordId));
            stringBuf.append("  ").append(recordName).append(" [");
            stringBuf.append(HexDump.toHex(options));
            stringBuf.append(',');
            stringBuf.append(HexDump.toHex(recordBytesRemaining));
            stringBuf.append("]  instance: ");
            stringBuf.append(HexDump.toHex((short) (options >> 4)));
            out.println(stringBuf);
            stringBuf.setLength(0);
            if (recordId == -4089 && 36 <= remainingBytes && 36 <= recordBytesRemaining) {
                StringBuilder stringBuf2 = stringBuf.append("    btWin32: ");
                byte n8 = (byte) in.read();
                stringBuf2.append(HexDump.toHex(n8));
                stringBuf2.append(getBlipType(n8));
                stringBuf2.append("  btMacOS: ");
                byte n82 = (byte) in.read();
                stringBuf2.append(HexDump.toHex(n82));
                stringBuf2.append(getBlipType(n82));
                out.println(stringBuf2);
                out.println("    rgbUid:");
                HexDump.dump(in, out, 0, 16);
                out.print("    tag: ");
                outHex(2, in, out);
                out.println();
                out.print("    size: ");
                outHex(4, in, out);
                out.println();
                out.print("    cRef: ");
                outHex(4, in, out);
                out.println();
                out.print("    offs: ");
                outHex(4, in, out);
                out.println();
                out.print("    usage: ");
                outHex(1, in, out);
                out.println();
                out.print("    cbName: ");
                outHex(1, in, out);
                out.println();
                out.print("    unused2: ");
                outHex(1, in, out);
                out.println();
                out.print("    unused3: ");
                outHex(1, in, out);
                out.println();
                remainingBytes -= 36;
                recordBytesRemaining = 0;
                atEOF = isContainer;
                recordName2 = recordName;
            } else if (recordId == -4080 && 18 <= remainingBytes && 18 <= recordBytesRemaining) {
                out.print("    Flag: ");
                outHex(2, in, out);
                out.println();
                out.print("    Col1: ");
                outHex(2, in, out);
                out.print("    dX1: ");
                outHex(2, in, out);
                out.print("    Row1: ");
                outHex(2, in, out);
                out.print("    dY1: ");
                outHex(2, in, out);
                out.println();
                out.print("    Col2: ");
                outHex(2, in, out);
                out.print("    dX2: ");
                outHex(2, in, out);
                out.print("    Row2: ");
                outHex(2, in, out);
                out.print("    dY2: ");
                outHex(2, in, out);
                out.println();
                remainingBytes -= 18;
                recordBytesRemaining -= 18;
                atEOF = isContainer;
                recordName2 = recordName;
            } else if (recordId == -4085 || recordId == -3806) {
                atEOF = isContainer;
                recordName2 = recordName;
                int nComplex = 0;
                out.println("    PROPID        VALUE");
                while (recordBytesRemaining >= nComplex + 6 && remainingBytes >= nComplex + 6) {
                    short n16 = LittleEndian.readShort(in);
                    int n32 = LittleEndian.readInt(in);
                    recordBytesRemaining -= 6;
                    long remainingBytes2 = remainingBytes - 6;
                    out.print("    ");
                    out.print(HexDump.toHex(n16));
                    out.print(" (");
                    int propertyId = n16 & 16383;
                    out.print(" " + propertyId);
                    if ((n16 & Short.MIN_VALUE) == 0) {
                        if ((n16 & 16384) != 0) {
                            out.print(", fBlipID");
                        }
                        out.print(")  ");
                        out.print(HexDump.toHex(n32));
                        if ((n16 & 16384) == 0) {
                            out.print(" (");
                            out.print(dec1616(n32));
                            out.print(')');
                            out.print(" {" + propName((short) propertyId) + "}");
                        }
                        out.println();
                    } else {
                        out.print(", fComplex)  ");
                        out.print(HexDump.toHex(n32));
                        out.print(" - Complex prop len");
                        out.println(" {" + propName((short) propertyId) + "}");
                        nComplex += n32;
                    }
                    remainingBytes = remainingBytes2;
                }
                while ((((long) nComplex) & remainingBytes) > 0) {
                    short nDumpSize = nComplex > ((int) remainingBytes) ? (short) remainingBytes : (short) nComplex;
                    HexDump.dump(in, out, 0, nDumpSize);
                    nComplex -= nDumpSize;
                    recordBytesRemaining -= nDumpSize;
                    remainingBytes -= (long) nDumpSize;
                }
            } else if (recordId == -4078) {
                out.print("    Connector rule: ");
                out.print(LittleEndian.readInt(in));
                out.print("    ShapeID A: ");
                out.print(LittleEndian.readInt(in));
                out.print("   ShapeID B: ");
                out.print(LittleEndian.readInt(in));
                out.print("    ShapeID connector: ");
                out.print(LittleEndian.readInt(in));
                out.print("   Connect pt A: ");
                out.print(LittleEndian.readInt(in));
                out.print("   Connect pt B: ");
                out.println(LittleEndian.readInt(in));
                recordBytesRemaining -= 24;
                remainingBytes -= 24;
                atEOF = isContainer;
                recordName2 = recordName;
            } else if (recordId < -4072 || recordId >= -3817) {
                atEOF = isContainer;
                recordName2 = recordName;
            } else {
                out.println("    Secondary UID: ");
                HexDump.dump(in, out, 0, 16);
                out.println("    Cache of size: " + HexDump.toHex(LittleEndian.readInt(in)));
                out.println("    Boundary top: " + HexDump.toHex(LittleEndian.readInt(in)));
                out.println("    Boundary left: " + HexDump.toHex(LittleEndian.readInt(in)));
                out.println("    Boundary width: " + HexDump.toHex(LittleEndian.readInt(in)));
                out.println("    Boundary height: " + HexDump.toHex(LittleEndian.readInt(in)));
                out.println("    X: " + HexDump.toHex(LittleEndian.readInt(in)));
                out.println("    Y: " + HexDump.toHex(LittleEndian.readInt(in)));
                out.println("    Cache of saved size: " + HexDump.toHex(LittleEndian.readInt(in)));
                out.println("    Compression Flag: " + HexDump.toHex((byte) in.read()));
                out.println("    Filter: " + HexDump.toHex((byte) in.read()));
                out.println("    Data (after decompression): ");
                int recordBytesRemaining2 = recordBytesRemaining - 50;
                long remainingBytes3 = remainingBytes - 50;
                int i = recordBytesRemaining2 > ((int) remainingBytes3) ? (short) remainingBytes3 : (short) recordBytesRemaining2;
                byte[] buf = new byte[i];
                int read = in.read(buf);
                while (read != -1 && read < i) {
                    read += in.read(buf, read, buf.length);
                }
                ByteArrayInputStream bin = new ByteArrayInputStream(buf);
                InputStream in1 = new InflaterInputStream(bin);
                atEOF = isContainer;
                recordName2 = recordName;
                HexDump.dump(in1, out, 0, -1);
                recordBytesRemaining = recordBytesRemaining2 - i;
                remainingBytes = remainingBytes3 - ((long) i);
            }
            boolean isContainer2 = (options & 15) == 15;
            if (!isContainer2 || remainingBytes < 0) {
                if (remainingBytes >= 0) {
                    short nDumpSize2 = recordBytesRemaining > ((int) remainingBytes) ? (short) remainingBytes : (short) recordBytesRemaining;
                    if (nDumpSize2 != 0) {
                        HexDump.dump(in, out, 0, nDumpSize2);
                        remainingBytes -= (long) nDumpSize2;
                    }
                } else {
                    out.println(" >> OVERRUN <<");
                }
            } else if (recordBytesRemaining <= ((int) remainingBytes)) {
                out.println("            completed within");
            } else {
                out.println("            continued elsewhere");
            }
            isContainer = atEOF;
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.ddf.EscherDump$1PropName, reason: invalid class name */
    final class C1PropName {
        final int _id;
        final String _name;

        public C1PropName(int id, String name) {
            this._id = id;
            this._name = name;
        }
    }

    private String propName(short propertyId) {
        C1PropName[] props = {new C1PropName(4, "transform.rotation"), new C1PropName(119, "protection.lockrotation"), new C1PropName(120, "protection.lockaspectratio"), new C1PropName(121, "protection.lockposition"), new C1PropName(122, "protection.lockagainstselect"), new C1PropName(123, "protection.lockcropping"), new C1PropName(124, "protection.lockvertices"), new C1PropName(125, "protection.locktext"), new C1PropName(126, "protection.lockadjusthandles"), new C1PropName(127, "protection.lockagainstgrouping"), new C1PropName(128, "text.textid"), new C1PropName(129, "text.textleft"), new C1PropName(130, "text.texttop"), new C1PropName(131, "text.textright"), new C1PropName(132, "text.textbottom"), new C1PropName(133, "text.wraptext"), new C1PropName(134, "text.scaletext"), new C1PropName(135, "text.anchortext"), new C1PropName(136, "text.textflow"), new C1PropName(137, "text.fontrotation"), new C1PropName(138, "text.idofnextshape"), new C1PropName(139, "text.bidir"), new C1PropName(187, "text.singleclickselects"), new C1PropName(HSSFShapeTypes.DoubleWave, "text.usehostmargins"), new C1PropName(HSSFShapeTypes.ActionButtonBlank, "text.rotatetextwithshape"), new C1PropName(HSSFShapeTypes.ActionButtonHome, "text.sizeshapetofittext"), new C1PropName(HSSFShapeTypes.ActionButtonHelp, "text.sizetexttofitshape"), new C1PropName(HSSFShapeTypes.ActionButtonInformation, "geotext.unicode"), new C1PropName(HSSFShapeTypes.ActionButtonForwardNext, "geotext.rtftext"), new C1PropName(HSSFShapeTypes.ActionButtonBackPrevious, "geotext.alignmentoncurve"), new C1PropName(HSSFShapeTypes.ActionButtonEnd, "geotext.defaultpointsize"), new C1PropName(HSSFShapeTypes.ActionButtonBeginning, "geotext.textspacing"), new C1PropName(HSSFShapeTypes.ActionButtonReturn, "geotext.fontfamilyname"), new C1PropName(240, "geotext.reverseroworder"), new C1PropName(241, "geotext.hastexteffect"), new C1PropName(242, "geotext.rotatecharacters"), new C1PropName(243, "geotext.kerncharacters"), new C1PropName(244, "geotext.tightortrack"), new C1PropName(245, "geotext.stretchtofitshape"), new C1PropName(246, "geotext.charboundingbox"), new C1PropName(247, "geotext.scaletextonpath"), new C1PropName(248, "geotext.stretchcharheight"), new C1PropName(249, "geotext.nomeasurealongpath"), new C1PropName(ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, "geotext.boldfont"), new C1PropName(251, "geotext.italicfont"), new C1PropName(252, "geotext.underlinefont"), new C1PropName(253, "geotext.shadowfont"), new C1PropName(254, "geotext.smallcapsfont"), new C1PropName(255, "geotext.strikethroughfont"), new C1PropName(256, "blip.cropfromtop"), new C1PropName(InputDeviceCompat.SOURCE_KEYBOARD, "blip.cropfrombottom"), new C1PropName(258, "blip.cropfromleft"), new C1PropName(259, "blip.cropfromright"), new C1PropName(260, "blip.bliptodisplay"), new C1PropName(261, "blip.blipfilename"), new C1PropName(262, "blip.blipflags"), new C1PropName(263, "blip.transparentcolor"), new C1PropName(264, "blip.contrastsetting"), new C1PropName(265, "blip.brightnesssetting"), new C1PropName(266, "blip.gamma"), new C1PropName(267, "blip.pictureid"), new C1PropName(268, "blip.doublemod"), new C1PropName(269, "blip.picturefillmod"), new C1PropName(270, "blip.pictureline"), new C1PropName(271, "blip.printblip"), new C1PropName(272, "blip.printblipfilename"), new C1PropName(273, "blip.printflags"), new C1PropName(316, "blip.nohittestpicture"), new C1PropName(317, "blip.picturegray"), new C1PropName(318, "blip.picturebilevel"), new C1PropName(319, "blip.pictureactive"), new C1PropName(320, "geometry.left"), new C1PropName(321, "geometry.top"), new C1PropName(322, "geometry.right"), new C1PropName(323, "geometry.bottom"), new C1PropName(324, "geometry.shapepath"), new C1PropName(325, "geometry.vertices"), new C1PropName(326, "geometry.segmentinfo"), new C1PropName(327, "geometry.adjustvalue"), new C1PropName(328, "geometry.adjust2value"), new C1PropName(329, "geometry.adjust3value"), new C1PropName(330, "geometry.adjust4value"), new C1PropName(331, "geometry.adjust5value"), new C1PropName(332, "geometry.adjust6value"), new C1PropName(333, "geometry.adjust7value"), new C1PropName(334, "geometry.adjust8value"), new C1PropName(335, "geometry.adjust9value"), new C1PropName(336, "geometry.adjust10value"), new C1PropName(378, "geometry.shadowOK"), new C1PropName(379, "geometry.3dok"), new C1PropName(380, "geometry.lineok"), new C1PropName(381, "geometry.geotextok"), new C1PropName(382, "geometry.fillshadeshapeok"), new C1PropName(383, "geometry.fillok"), new C1PropName(384, "fill.filltype"), new C1PropName(385, "fill.fillcolor"), new C1PropName(386, "fill.fillopacity"), new C1PropName(387, "fill.fillbackcolor"), new C1PropName(388, "fill.backopacity"), new C1PropName(389, "fill.crmod"), new C1PropName(390, "fill.patterntexture"), new C1PropName(391, "fill.blipfilename"), new C1PropName(392, "fill.blipflags"), new C1PropName(393, "fill.width"), new C1PropName(394, "fill.height"), new C1PropName(395, "fill.angle"), new C1PropName(396, "fill.focus"), new C1PropName(397, "fill.toleft"), new C1PropName(398, "fill.totop"), new C1PropName(399, "fill.toright"), new C1PropName(400, "fill.tobottom"), new C1PropName(401, "fill.rectleft"), new C1PropName(402, "fill.recttop"), new C1PropName(403, "fill.rectright"), new C1PropName(404, "fill.rectbottom"), new C1PropName(405, "fill.dztype"), new C1PropName(406, "fill.shadepreset"), new C1PropName(407, "fill.shadecolors"), new C1PropName(408, "fill.originx"), new C1PropName(409, "fill.originy"), new C1PropName(410, "fill.shapeoriginx"), new C1PropName(411, "fill.shapeoriginy"), new C1PropName(412, "fill.shadetype"), new C1PropName(443, "fill.filled"), new C1PropName(444, "fill.hittestfill"), new C1PropName(445, "fill.shape"), new C1PropName(446, "fill.userect"), new C1PropName(447, "fill.nofillhittest"), new C1PropName(448, "linestyle.color"), new C1PropName(449, "linestyle.opacity"), new C1PropName(450, "linestyle.backcolor"), new C1PropName(451, "linestyle.crmod"), new C1PropName(452, "linestyle.linetype"), new C1PropName(453, "linestyle.fillblip"), new C1PropName(454, "linestyle.fillblipname"), new C1PropName(455, "linestyle.fillblipflags"), new C1PropName(456, "linestyle.fillwidth"), new C1PropName(457, "linestyle.fillheight"), new C1PropName(458, "linestyle.filldztype"), new C1PropName(459, "linestyle.linewidth"), new C1PropName(460, "linestyle.linemiterlimit"), new C1PropName(461, "linestyle.linestyle"), new C1PropName(462, "linestyle.linedashing"), new C1PropName(463, "linestyle.linedashstyle"), new C1PropName(464, "linestyle.linestartarrowhead"), new C1PropName(465, "linestyle.lineendarrowhead"), new C1PropName(466, "linestyle.linestartarrowwidth"), new C1PropName(467, "linestyle.lineestartarrowlength"), new C1PropName(468, "linestyle.lineendarrowwidth"), new C1PropName(469, "linestyle.lineendarrowlength"), new C1PropName(470, "linestyle.linejoinstyle"), new C1PropName(471, "linestyle.lineendcapstyle"), new C1PropName(507, "linestyle.arrowheadsok"), new C1PropName(508, "linestyle.anyline"), new C1PropName(509, "linestyle.hitlinetest"), new C1PropName(510, "linestyle.linefillshape"), new C1PropName(FrameMetricsAggregator.EVERY_DURATION, "linestyle.nolinedrawdash"), new C1PropName(512, "shadowstyle.type"), new C1PropName(InputDeviceCompat.SOURCE_DPAD, "shadowstyle.color"), new C1PropName(514, "shadowstyle.highlight"), new C1PropName(515, "shadowstyle.crmod"), new C1PropName(516, "shadowstyle.opacity"), new C1PropName(517, "shadowstyle.offsetx"), new C1PropName(518, "shadowstyle.offsety"), new C1PropName(519, "shadowstyle.secondoffsetx"), new C1PropName(520, "shadowstyle.secondoffsety"), new C1PropName(521, "shadowstyle.scalextox"), new C1PropName(522, "shadowstyle.scaleytox"), new C1PropName(523, "shadowstyle.scalextoy"), new C1PropName(524, "shadowstyle.scaleytoy"), new C1PropName(525, "shadowstyle.perspectivex"), new C1PropName(526, "shadowstyle.perspectivey"), new C1PropName(527, "shadowstyle.weight"), new C1PropName(528, "shadowstyle.originx"), new C1PropName(529, "shadowstyle.originy"), new C1PropName(574, "shadowstyle.shadow"), new C1PropName(575, "shadowstyle.shadowobsured"), new C1PropName(Units.MASTER_DPI, "perspective.type"), new C1PropName(577, "perspective.offsetx"), new C1PropName(578, "perspective.offsety"), new C1PropName(579, "perspective.scalextox"), new C1PropName(580, "perspective.scaleytox"), new C1PropName(581, "perspective.scalextoy"), new C1PropName(582, "perspective.scaleytox"), new C1PropName(583, "perspective.perspectivex"), new C1PropName(584, "perspective.perspectivey"), new C1PropName(585, "perspective.weight"), new C1PropName(586, "perspective.originx"), new C1PropName(587, "perspective.originy"), new C1PropName(639, "perspective.perspectiveon"), new C1PropName(640, "3d.specularamount"), new C1PropName(661, "3d.diffuseamount"), new C1PropName(662, "3d.shininess"), new C1PropName(663, "3d.edgethickness"), new C1PropName(664, "3d.extrudeforward"), new C1PropName(665, "3d.extrudebackward"), new C1PropName(666, "3d.extrudeplane"), new C1PropName(667, "3d.extrusioncolor"), new C1PropName(648, "3d.crmod"), new C1PropName(700, "3d.3deffect"), new C1PropName(701, "3d.metallic"), new C1PropName(702, "3d.useextrusioncolor"), new C1PropName(703, "3d.lightface"), new C1PropName(704, "3dstyle.yrotationangle"), new C1PropName(705, "3dstyle.xrotationangle"), new C1PropName(706, "3dstyle.rotationaxisx"), new C1PropName(707, "3dstyle.rotationaxisy"), new C1PropName(708, "3dstyle.rotationaxisz"), new C1PropName(709, "3dstyle.rotationangle"), new C1PropName(710, "3dstyle.rotationcenterx"), new C1PropName(711, "3dstyle.rotationcentery"), new C1PropName(712, "3dstyle.rotationcenterz"), new C1PropName(713, "3dstyle.rendermode"), new C1PropName(714, "3dstyle.tolerance"), new C1PropName(715, "3dstyle.xviewpoint"), new C1PropName(716, "3dstyle.yviewpoint"), new C1PropName(717, "3dstyle.zviewpoint"), new C1PropName(718, "3dstyle.originx"), new C1PropName(719, "3dstyle.originy"), new C1PropName(720, "3dstyle.skewangle"), new C1PropName(721, "3dstyle.skewamount"), new C1PropName(722, "3dstyle.ambientintensity"), new C1PropName(723, "3dstyle.keyx"), new C1PropName(724, "3dstyle.keyy"), new C1PropName(725, "3dstyle.keyz"), new C1PropName(726, "3dstyle.keyintensity"), new C1PropName(727, "3dstyle.fillx"), new C1PropName(728, "3dstyle.filly"), new C1PropName(729, "3dstyle.fillz"), new C1PropName(730, "3dstyle.fillintensity"), new C1PropName(763, "3dstyle.constrainrotation"), new C1PropName(764, "3dstyle.rotationcenterauto"), new C1PropName(765, "3dstyle.parallel"), new C1PropName(766, "3dstyle.keyharsh"), new C1PropName(767, "3dstyle.fillharsh"), new C1PropName(769, "shape.master"), new C1PropName(771, "shape.connectorstyle"), new C1PropName(772, "shape.blackandwhitesettings"), new C1PropName(773, "shape.wmodepurebw"), new C1PropName(774, "shape.wmodebw"), new C1PropName(826, "shape.oleicon"), new C1PropName(827, "shape.preferrelativeresize"), new C1PropName(828, "shape.lockshapetype"), new C1PropName(830, "shape.deleteattachedobject"), new C1PropName(831, "shape.backgroundshape"), new C1PropName(832, "callout.callouttype"), new C1PropName(833, "callout.xycalloutgap"), new C1PropName(834, "callout.calloutangle"), new C1PropName(835, "callout.calloutdroptype"), new C1PropName(836, "callout.calloutdropspecified"), new C1PropName(837, "callout.calloutlengthspecified"), new C1PropName(889, "callout.iscallout"), new C1PropName(890, "callout.calloutaccentbar"), new C1PropName(891, "callout.callouttextborder"), new C1PropName(892, "callout.calloutminusx"), new C1PropName(893, "callout.calloutminusy"), new C1PropName(894, "callout.dropauto"), new C1PropName(895, "callout.lengthspecified"), new C1PropName(896, "groupshape.shapename"), new C1PropName(897, "groupshape.description"), new C1PropName(898, "groupshape.hyperlink"), new C1PropName(899, "groupshape.wrappolygonvertices"), new C1PropName(900, "groupshape.wrapdistleft"), new C1PropName(901, "groupshape.wrapdisttop"), new C1PropName(902, "groupshape.wrapdistright"), new C1PropName(903, "groupshape.wrapdistbottom"), new C1PropName(904, "groupshape.regroupid"), new C1PropName(953, "groupshape.editedwrap"), new C1PropName(954, "groupshape.behinddocument"), new C1PropName(955, "groupshape.ondblclicknotify"), new C1PropName(956, "groupshape.isbutton"), new C1PropName(957, "groupshape.1dadjustment"), new C1PropName(958, "groupshape.hidden"), new C1PropName(959, "groupshape.print")};
        for (int i = 0; i < props.length; i++) {
            if (props[i]._id == propertyId) {
                return props[i]._name;
            }
        }
        return "unknown property";
    }

    private static String getBlipType(byte b) {
        return EscherBSERecord.getBlipType(b);
    }

    private String dec1616(int n32) {
        String result = "" + ((int) ((short) (n32 >> 16)));
        return (result + '.') + ((int) ((short) (65535 & n32)));
    }

    private void outHex(int bytes, InputStream in, PrintStream out) throws IOException {
        if (bytes == 1) {
            out.print(HexDump.toHex((byte) in.read()));
        } else if (bytes == 2) {
            out.print(HexDump.toHex(LittleEndian.readShort(in)));
        } else {
            if (bytes == 4) {
                out.print(HexDump.toHex(LittleEndian.readInt(in)));
                return;
            }
            throw new IOException("Unable to output variable of that width");
        }
    }

    public static void main(String[] args) {
        main(args, System.out);
    }

    public static void main(String[] args, PrintStream out) {
        byte[] bytes = HexRead.readFromString("0F 00 00 F0 89 07 00 00 00 00 06 F0 18 00 00 00 05 04 00 00 02 00 00 00 05 00 00 00 01 00 00 00 01 00 00 00 05 00 00 00 4F 00 01 F0 2F 07 00 00 42 00 07 F0 B7 01 00 00 03 04 3F 14 AE 6B 0F 65 B0 48 BF 5E 94 63 80 E8 91 73 FF 00 93 01 00 00 01 00 00 00 00 00 00 00 00 00 FF FF 20 54 1C F0 8B 01 00 00 3F 14 AE 6B 0F 65 B0 48 BF 5E 94 63 80 E8 91 73 92 0E 00 00 00 00 00 00 00 00 00 00 D1 07 00 00 DD 05 00 00 4A AD 6F 00 8A C5 53 00 59 01 00 00 00 FE 78 9C E3 9B C4 00 04 AC 77 D9 2F 32 08 32 FD E7 61 F8 FF 0F C8 FD 05 C5 30 19 10 90 63 90 FA 0F 06 0C 8C 0C 5C 70 19 43 30 EB 0E FB 05 86 85 0C DB 18 58 80 72 8C 70 16 0B 83 05 56 51 29 88 C9 60 D9 69 0C 6C 20 26 23 03 C8 74 B0 A8 0E 03 07 FB 45 56 C7 A2 CC C4 1C 06 66 A0 0D 2C 40 39 5E 86 4C 06 3D A0 4E 10 D0 60 D9 C8 58 CC E8 CF B0 80 61 3A 8A 7E 0D C6 23 AC 4F E0 E2 98 B6 12 2B 06 73 9D 12 E3 52 56 59 F6 08 8A CC 52 66 A3 50 FF 96 2B 94 E9 DF 4C A1 FE 2D 3A 03 AB 9F 81 C2 F0 A3 54 BF 0F 85 EE A7 54 FF 40 FB 7F A0 E3 9F D2 F4 4F 71 FE 19 58 FF 2B 31 7F 67 36 3B 25 4F 99 1B 4E 53 A6 5F 89 25 95 E9 C4 00 C7 83 12 F3 1F 26 35 4A D3 D2 47 0E 0A C3 41 8E C9 8A 52 37 DC 15 A1 D0 0D BC 4C 06 0C 2B 28 2C 13 28 D4 EF 43 61 5A A0 58 3F 85 71 E0 4B 69 9E 64 65 FE 39 C0 E5 22 30 1D 30 27 0E 74 3A 18 60 FD 4A CC B1 2C 13 7D 07 36 2D 2A 31 85 B2 6A 0D 74 1D 1D 22 4D 99 FE 60 0A F5 9B EC 1C 58 FD 67 06 56 3F 38 0D 84 3C A5 30 0E 28 D3 AF C4 A4 CA FA 44 7A 0D 65 6E 60 7F 4D A1 1B 24 58 F7 49 AF A5 CC 0D CC DF 19 FE 03 00 F0 B1 25 4D 42 00 07 F0 E1 01 00 00 03 04 39 50 BE 98 B0 6F 57 24 31 70 5D 23 2F 9F 10 66 FF 00 BD 01 00 00 01 00 00 00 00 00 00 00 00 00 FF FF 20 54 1C F0 B5 01 00 00 39 50 BE 98 B0 6F 57 24 31 70 5D 23 2F 9F 10 66 DA 03 00 00 00 00 00 00 00 00 00 00 D1 07 00 00 DD 05 00 00 4A AD 6F 00 8A C5 53 00 83 01 00 00 00 FE 78 9C A5 52 BF 4B 42 51 14 3E F7 DC 77 7A 16 45 48 8B 3C 48 A8 16 15 0D 6C 88 D0 04 C3 40 A3 32 1C 84 96 08 21 04 A1 C5 5C A2 35 82 C0 35 6A AB 1C 6A 6B A8 24 5A 83 68 08 84 84 96 A2 86 A0 7F C2 86 5E E7 5E F5 41 E4 10 BC 03 1F E7 FB F1 CE B9 F7 F1 9E 7C 05 2E 7A 37 9B E0 45 7B 10 EC 6F 96 5F 1D 74 13 55 7E B0 6C 5D 20 60 C0 49 A2 9A BD 99 4F 50 83 1B 30 38 13 0E 33 60 A6 A7 6B B5 37 EB F4 10 FA 14 15 A0 B6 6B 37 0C 1E B3 49 73 5B A5 C2 26 48 3E C1 E0 6C 08 4A 30 C9 93 AA 02 B8 20 13 62 05 4E E1 E8 D7 7C C0 B8 14 95 5E BE B8 A7 CF 1E BE 55 2C 56 B9 78 DF 08 7E 88 4C 27 FF 7B DB FF 7A DD B7 1A 17 67 34 6A AE BA DA 35 D1 E7 72 BE FE EC 6E FE DA E5 7C 3D EC 7A DE 03 FD 50 06 0B 23 F2 0E F3 B2 A5 11 91 0D 4C B5 B5 F3 BF 94 C1 8F 24 F7 D9 6F 60 94 3B C9 9A F3 1C 6B E7 BB F0 2E 49 B2 25 2B C6 B1 EE 69 EE 15 63 4F 71 7D CE 85 CC C8 35 B9 C3 28 28 CE D0 5C 67 79 F2 4A A2 14 23 A4 38 43 73 9D 2D 69 2F C1 08 31 9F C5 5C 9B EB 7B C5 69 19 B3 B4 81 F3 DC E3 B4 8E 8B CC B3 94 53 5A E7 41 2A 63 9A AA 38 C5 3D 48 BB EC 57 59 6F 2B AD 73 1F 1D 60 92 AE 70 8C BB 8F CE 31 C1 3C 49 27 4A EB DC A4 5B 8C D1 0B 0E 73 37 E9 11 A7 99 C7 E8 41 69 B0 7F 00 96 F2 A7 E8 42 00 07 F0 B4 01 00 00 03 04 1A BA F9 D6 A9 B9 3A 03 08 61 E9 90 FF 7B 9E E6 FF 00 90 01 00 00 01 00 00 00 00 00 00 00 00 00 FF FF 20 54 1C F0 88 01 00 00 1A BA F9 D6 A9 B9 3A 03 08 61 E9 90 FF 7B 9E E6 12 0E 00 00 00 00 00 00 00 00 00 00 D1 07 00 00 DD 05 00 00 4A AD 6F 00 8A C5 53 00 56 01 00 00 00 FE 78 9C E3 13 62 00 02 D6 BB EC 17 19 04 99 FE F3 30 FC FF 07 E4 FE 82 62 98 0C 08 C8 31 48 FD 07 03 06 46 06 2E B8 8C 21 98 75 87 FD 02 C3 42 86 6D 0C 2C 40 39 46 38 8B 85 C1 02 AB A8 14 C4 64 B0 EC 34 06 36 10 93 91 01 64 3A 58 54 87 81 83 FD 22 AB 63 51 66 62 0E 03 33 D0 06 16 A0 1C 2F 43 26 83 1E 50 27 08 68 B0 6C 64 2C 66 F4 67 58 C0 30 1D 45 BF 06 E3 11 D6 27 70 71 4C 5B 89 15 83 B9 4E 89 71 29 AB 2C 7B 04 45 66 29 B3 51 A8 7F CB 15 CA F4 6F A6 50 FF 16 9D 81 D5 CF 40 61 F8 51 AA DF 87 42 F7 53 AA 7F A0 FD 3F D0 F1 4F 69 FA A7 38 FF 0C AC FF 95 98 BF 33 9B 9D 92 A7 CC 0D A7 29 D3 AF C4 92 CA 74 62 80 E3 41 89 F9 0F 93 1A A5 69 E9 23 07 85 E1 20 C7 64 45 A9 1B EE 8A 50 E8 06 5E 26 03 86 15 14 96 09 14 EA F7 A1 30 2D 50 AC 9F C2 38 F0 A5 34 4F B2 32 FF 1C E0 72 11 98 0E 98 13 07 38 1D 28 31 C7 B2 4C F4 1D D8 B4 A0 C4 14 CA AA 35 D0 75 64 88 34 65 FA 83 29 D4 6F B2 73 60 F5 9F A1 54 FF 0E CA D3 40 C8 53 0A E3 E0 09 85 6E 50 65 7D 22 BD 86 32 37 B0 BF A6 D0 0D 12 AC FB A4 D7 52 E6 06 E6 EF 0C FF 01 97 1D 12 C7 42 00 07 F0 C3 01 00 00 03 04 BA 4C B6 23 BA 8B 27 BE C8 55 59 86 24 9F 89 D4 FF 00 9F 01 00 00 01 00 00 00 00 00 00 00 00 00 FF FF 20 54 1C F0 97 01 00 00 BA 4C B6 23 BA 8B 27 BE C8 55 59 86 24 9F 89 D4 AE 0E 00 00 00 00 00 00 00 00 00 00 D1 07 00 00 DD 05 00 00 4A AD 6F 00 8A C5 53 00 65 01 00 00 00 FE 78 9C E3 5B C7 00 04 AC 77 D9 2F 32 08 32 FD E7 61 F8 FF 0F C8 FD 05 C5 30 19 10 90 63 90 FA 0F 06 0C 8C 0C 5C 70 19 43 30 EB 0E FB 05 86 85 0C DB 18 58 80 72 8C 70 16 0B 83 05 56 51 29 88 C9 60 D9 69 0C 6C 20 26 23 03 C8 74 B0 A8 0E 03 07 FB 45 56 C7 A2 CC C4 1C 06 66 A0 0D 2C 40 39 5E 86 4C 06 3D A0 4E 10 D0 60 99 C6 B8 98 D1 9F 61 01 C3 74 14 FD 1A 8C 2B D8 84 B1 88 4B A5 A5 75 03 01 50 DF 59 46 77 46 0F A8 3C A6 AB 88 15 83 B9 5E 89 B1 8B D5 97 2D 82 22 B3 94 29 D5 BF E5 CA C0 EA DF AC 43 A1 FD 14 EA 67 A0 30 FC 28 D5 EF 43 A1 FB 7D 87 B8 FF 07 3A FE 07 3A FD 53 EA 7E 0A C3 4F 89 F9 0E 73 EA 69 79 CA DC 70 8A 32 FD 4A 2C 5E 4C DF 87 7A 3C BC E0 A5 30 1E 3E 31 C5 33 AC A0 30 2F 52 A8 DF 87 C2 30 A4 54 3F A5 65 19 85 65 A9 12 D3 2B 16 0D 8A CB 13 4A F3 E3 27 E6 09 03 9D 0E 06 58 BF 12 B3 13 CB C1 01 4E 8B 4A 4C 56 AC 91 03 5D 37 86 48 53 A6 3F 98 42 FD 26 3B 07 56 FF 99 1D 14 EA A7 CC 7E 70 1A 08 79 42 61 1C 3C A5 D0 0D 9C 6C C2 32 6B 29 73 03 DB 6B CA DC C0 F8 97 F5 AD CC 1A CA DC C0 F4 83 32 37 B0 A4 30 CE FC C7 48 99 1B FE 33 32 FC 07 00 6C CC 2E 23 33 00 0B F0 12 00 00 00 BF 00 08 00 08 00 81 01 09 00 00 08 C0 01 40 00 00 08 40 00 1E F1 10 00 00 00 0D 00 00 08 0C 00 00 08 17 00 00 08 F7 00 00 10                                              ");
        EscherDump dumper = new EscherDump();
        dumper.dump(bytes, 0, bytes.length, out);
    }

    public void dump(int recordSize, byte[] data, PrintStream out) {
        dump(data, 0, recordSize, out);
    }
}
