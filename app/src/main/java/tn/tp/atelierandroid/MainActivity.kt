package tn.tp.atelierandroid

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val TAG = "AtelierCycle"

    private val participants = mutableListOf<Participant>()
    private val participantsAffiches = mutableListOf<Participant>()
    private lateinit var adaptateur: ArrayAdapter<String>

    private lateinit var spFiltreGroupe: Spinner
    private lateinit var lvParticipants: ListView
    private var selectionne: Participant? = null

    private var rafraichirVue: (() -> Unit)? = null

    companion object {
        private const val CLE_PARTICIPANTS = "cle_participants"
        private const val CLE_FILTRE = "cle_filtre"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_main)

        // ---- Composants du formulaire ----
        val etNom = findViewById<EditText>(R.id.etNom)
        val etPrenom = findViewById<EditText>(R.id.etPrenom)
        val etAge = findViewById<EditText>(R.id.etAge)
        val spGroupe = findViewById<Spinner>(R.id.spGroupe)
        val rgSupport = findViewById<RadioGroup>(R.id.rgSupport)
        val cbConfirmation = findViewById<CheckBox>(R.id.cbConfirmation)

        // ---- Boutons ----
        val btnAfficher = findViewById<Button>(R.id.btnAfficher)
        val btnEffacer = findViewById<Button>(R.id.btnEffacer)
        val btnEnregistrer = findViewById<Button>(R.id.btnEnregistrer)
        val btnListe = findViewById<Button>(R.id.btnListe)
        val btnRechercher = findViewById<Button>(R.id.btnRechercher)
        val btnOptions = findViewById<Button>(R.id.btnOptions)
        val btnModifier = findViewById<Button>(R.id.btnModifier)
        val btnSupprimer = findViewById<Button>(R.id.btnSupprimer)

        // ---- Affichage ----
        val tvResultat = findViewById<TextView>(R.id.tvResultat)
        val tvCompteur = findViewById<TextView>(R.id.tvCompteur)
        val etRecherche = findViewById<EditText>(R.id.etRecherche)

        spFiltreGroupe = findViewById(R.id.spFiltreGroupe)
        lvParticipants = findViewById(R.id.lvParticipants)

        adaptateur = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        lvParticipants.adapter = adaptateur

        // ---- Restaurer les participants avant la première actualisation ----
        restaurerParticipants(savedInstanceState)

        fun actualiserAffichage() {
            val filtre = spFiltreGroupe.selectedItem?.toString() ?: "Tous"
            participantsAffiches.clear()
            for (p in participants) {
                if (filtre == "Tous" || p.groupe == filtre) participantsAffiches.add(p)
            }
            val libelles = participantsAffiches.map { it.libelleCourt() }
            adaptateur.clear()
            adaptateur.addAll(libelles)
            adaptateur.notifyDataSetChanged()
            tvCompteur.text = "Inscriptions : ${participants.size}"
        }
        rafraichirVue = { actualiserAffichage() }

        // Restaurer le filtre
        val filtreRestaure = savedInstanceState?.getInt(CLE_FILTRE, 0)?.coerceIn(0, 3) ?: 0
        spFiltreGroupe.setSelection(filtreRestaure)

        spFiltreGroupe.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                rafraichirVue?.invoke()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        fun annulerSelection() {
            tvResultat.text = getString(R.string.resultat_initial)
            etNom.text.clear()
            etPrenom.text.clear()
            etAge.text.clear()
            spGroupe.setSelection(0)
            rgSupport.check(R.id.rbEmulateur)
            cbConfirmation.isChecked = false
            selectionne = null
        }

        // PopupMenu
        btnOptions.setOnClickListener {
            val popup = PopupMenu(this, btnOptions)
            popup.menuInflater.inflate(R.menu.menu_atelier, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_tous -> { annulerSelection(); spFiltreGroupe.setSelection(0) }
                    R.id.action_g1 -> { annulerSelection(); spFiltreGroupe.setSelection(1) }
                    R.id.action_g2 -> { annulerSelection(); spFiltreGroupe.setSelection(2) }
                    R.id.action_g3 -> { annulerSelection(); spFiltreGroupe.setSelection(3) }
                    R.id.action_apropos -> {
                        AlertDialog.Builder(this)
                            .setTitle("À propos")
                            .setMessage("Atelier Android — Développement Mobile (ING-A3-GL-AL-04)")
                            .setPositiveButton("Fermer", null)
                            .show()
                    }
                }
                true
            }
            popup.show()
        }

        fun lireParticipant(): Participant? {
            val nom = etNom.text.toString().trim()
            val prenom = etPrenom.text.toString().trim()
            val age = etAge.text.toString().toIntOrNull()
            val groupe = spGroupe.selectedItem.toString()
            val support = if (rgSupport.checkedRadioButtonId == R.id.rbEmulateur) "Émulateur" else "Téléphone"
            if (nom.isEmpty() || prenom.isEmpty()) { Toast.makeText(this, "Nom et prénom obligatoires", Toast.LENGTH_SHORT).show(); return null }
            if (age == null || age !in 1..120) { Toast.makeText(this, "Âge invalide", Toast.LENGTH_SHORT).show(); return null }
            if (groupe == "Choisir un groupe") { Toast.makeText(this, "Choisissez un groupe", Toast.LENGTH_SHORT).show(); return null }
            if (!cbConfirmation.isChecked) { Toast.makeText(this, "Confirmez les informations", Toast.LENGTH_SHORT).show(); return null }
            return Participant(nom, prenom, age, groupe, support)
        }

        btnAfficher.setOnClickListener { val p = lireParticipant() ?: return@setOnClickListener; tvResultat.text = p.fiche() }

        btnEnregistrer.setOnClickListener {
            val p = lireParticipant() ?: return@setOnClickListener
            val doublon = participants.any { it.nom.equals(p.nom, true) && it.prenom.equals(p.prenom, true) }
            if (doublon) Toast.makeText(this, "Déjà inscrit", Toast.LENGTH_SHORT).show()
            else { participants.add(p); tvResultat.text = p.fiche(); rafraichirVue?.invoke() }
        }

        btnListe.setOnClickListener {
            if (participantsAffiches.isEmpty()) tvResultat.text = "Aucune inscription"
            else {
                val sb = StringBuilder()
                for (p in participantsAffiches) sb.appendLine(p.libelleCourt())
                tvResultat.text = sb.toString().trim()
            }
        }

        btnRechercher.setOnClickListener {
            val r = etRecherche.text.toString().trim()
            if (r.isEmpty()) { Toast.makeText(this, "Saisissez un nom", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            val trouve = participants.firstOrNull { it.nom.cleRecherche() == r.cleRecherche() }
            tvResultat.text = trouve?.fiche() ?: "Aucun participant trouvé"
        }

        btnEffacer.setOnClickListener { annulerSelection() }

        // Modifier
        btnModifier.setOnClickListener {
            val cible = selectionne
            if (cible == null) {
                Toast.makeText(this, "Sélectionnez d'abord un participant", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val nom = etNom.text.toString().trim()
            val prenom = etPrenom.text.toString().trim()
            val age = etAge.text.toString().toIntOrNull()
            val groupe = spGroupe.selectedItem.toString()
            if (nom.isEmpty() || prenom.isEmpty()) { Toast.makeText(this, "Nom et prénom obligatoires", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            if (age == null || age !in 1..120) { Toast.makeText(this, "Âge invalide", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            if (groupe == "Choisir un groupe") { Toast.makeText(this, "Choisissez un groupe", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            val doublon = participants.any { it !== cible && it.nom.equals(nom, true) && it.prenom.equals(prenom, true) }
            if (doublon) { Toast.makeText(this, "Un autre participant porte déjà ce nom", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            cible.nom = nom
            cible.prenom = prenom
            cible.age = age
            cible.groupe = groupe
            rafraichirVue?.invoke()
            tvResultat.text = cible.fiche()
            Toast.makeText(this, "Participant modifié", Toast.LENGTH_SHORT).show()
        }

        // Supprimer
        btnSupprimer.setOnClickListener {
            val cible = selectionne
            if (cible == null) {
                Toast.makeText(this, "Sélectionnez d'abord un participant", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AlertDialog.Builder(this)
                .setTitle("Confirmer la suppression")
                .setMessage("Supprimer ${cible.prenom} ${cible.nom} (${cible.groupe}) ?")
                .setPositiveButton("Supprimer") { _, _ ->
                    participants.remove(cible)
                    selectionne = null
                    annulerSelection()
                    rafraichirVue?.invoke()
                    Toast.makeText(this, "Participant supprimé", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Annuler", null)
                .show()
        }

        // Sélection d'une ligne
        lvParticipants.setOnItemClickListener { _, _, position, _ ->
            val p = participantsAffiches.getOrNull(position)
            if (p != null) {
                selectionne = p
                etNom.setText(p.nom)
                etPrenom.setText(p.prenom)
                etAge.setText(p.age.toString())
                val adapterGroupes = spGroupe.adapter as? ArrayAdapter<String>
                val indexGroupe = adapterGroupes?.getPosition(p.groupe) ?: 0
                spGroupe.setSelection(indexGroupe)
                if (p.support == "Émulateur") rgSupport.check(R.id.rbEmulateur) else rgSupport.check(R.id.rbTelephone)
                cbConfirmation.isChecked = true
                tvResultat.text = p.fiche()
            }
        }

        rafraichirVue?.invoke()
    }

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume") }
    override fun onPause() { super.onPause(); Log.d(TAG, "onPause") }
    override fun onStop() { super.onStop(); Log.d(TAG, "onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy") }

    override fun onSaveInstanceState(outState: Bundle) {
        val champs = ArrayList<String>()
        for (p in participants) {
            champs.add(p.nom)
            champs.add(p.prenom)
            champs.add(p.age.toString())
            champs.add(p.groupe)
            champs.add(p.support)
        }
        outState.putStringArrayList(CLE_PARTICIPANTS, champs)
        outState.putInt(CLE_FILTRE, spFiltreGroupe.selectedItemPosition)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val filtre = savedInstanceState.getInt(CLE_FILTRE, 0).coerceIn(0, 3)
        spFiltreGroupe.setSelection(filtre)
        rafraichirVue?.invoke()
    }

    private fun restaurerParticipants(etat: Bundle?) {
        val champs = etat?.getStringArrayList(CLE_PARTICIPANTS) ?: return
        if (champs.size % 5 != 0) return
        for (i in champs.indices step 5) {
            val nom = champs[i]
            val prenom = champs[i + 1]
            val age = champs[i + 2].toIntOrNull() ?: continue
            if (age !in 1..120) continue
            if (nom.isEmpty() || prenom.isEmpty()) continue
            val groupe = champs[i + 3]
            val support = champs[i + 4]
            val existant = participants.any { it.nom.equals(nom, true) && it.prenom.equals(prenom, true) }
            if (!existant) participants.add(Participant(nom, prenom, age, groupe, support))
        }
    }
}